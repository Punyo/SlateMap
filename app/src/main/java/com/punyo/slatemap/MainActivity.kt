package com.punyo.slatemap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.punyo.slatemap.data.unlockedlocality.UnlockedLocalityRepository
import com.punyo.slatemap.theme.SlateMapTheme
import com.punyo.slatemap.ui.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var unlockedLocalityRepository: UnlockedLocalityRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SlateMapTheme {
                Navigation()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                unlockedLocalityRepository.commitUnlockedLocalityChanges()
            }
        }
    }
}
