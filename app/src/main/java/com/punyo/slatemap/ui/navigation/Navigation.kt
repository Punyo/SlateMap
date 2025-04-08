package com.punyo.slatemap.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.punyo.slatemap.application.NavigationDestination
import com.punyo.slatemap.ui.map.MapScreen

@Composable
fun Navigation(modifier: Modifier = Modifier) {
    var currentNavState by remember {
        mutableStateOf(NavigationDestination.Map)
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            NavigationTopBar(
                currentNavTitle = "Temp",
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationDestination.entries.forEach { destination ->
                    NavigationBarItem(
                        label = { Text(text = destination.name) },
                        selected = destination == currentNavState,
                        icon = {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                            )
                        },
                        onClick = { currentNavState = destination },
                    )
                }
            }
        },
    ) { innerPadding ->
        when (currentNavState) {
            NavigationDestination.Map -> {
                MapScreen(
                    modifier = Modifier.padding(innerPadding),
                )
            }

            NavigationDestination.Temp1 -> {
                MapScreen(
                    modifier = Modifier.padding(innerPadding),
                )
            }

            NavigationDestination.Temp2 -> {
                MapScreen(
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NavigationTopBar(
    currentNavTitle: String,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        title = { Text(text = currentNavTitle) },
        colors =
            TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
    )
}

@Composable
private fun NavigationBottomBar(
    onItemClick: (NavigationDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
}
