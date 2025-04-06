package com.punyo.slatemap.ui.map

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    title: String = "Map",
    mapScreenViewModel: MapScreenViewModel = hiltViewModel(),
) {
    Text(
        text = title,
        modifier = modifier,
    )
}
