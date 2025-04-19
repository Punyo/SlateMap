package com.punyo.slatemap.ui.debug

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.punyo.slatemap.application.constant.MockLocationConstants

@Composable
fun DebugScreen(
    modifier: Modifier = Modifier,
    viewModel: DebugScreenViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    Column(modifier = modifier.fillMaxSize()) {
        Button(onClick = { viewModel.setMockLocation(location = MockLocationConstants.tokyo) }) {
            Text(text = "位置情報を東京に変更")
        }
        Button(onClick = { viewModel.setMockLocation(location = MockLocationConstants.nagoya) }) {
            Text(text = "位置情報を名古屋に変更")
        }
        Button(onClick = { viewModel.setMockLocation(location = MockLocationConstants.osaka) }) {
            Text(text = "位置情報を大阪に変更")
        }
        Button(onClick = { viewModel.clearMockLocation() }) {
            Text(text = "位置情報を実際の位置に戻す")
        }
        Text(text = "アプリ上での現在の位置情報: ${uiState.currentGeoInfoString}")
    }
}
