package com.punyo.slatemap.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Polygon
import com.punyo.slatemap.R
import com.punyo.slatemap.application.LatLngConstants

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    mapScreenViewModel: MapScreenViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val currentState = mapScreenViewModel.uiState.collectAsStateWithLifecycle()
    val style =
        remember { mutableStateOf(MapStyleOptions.loadRawResourceStyle(context, R.raw.style)) }
    GoogleMap(
        modifier = modifier,
        properties =
            MapProperties(
                minZoomPreference = 5.5f,
                latLngBoundsForCameraTarget = LatLngConstants.japanBoundsForCameraTarget,
            ),
        onMapLoaded = {
        },
        cameraPositionState = currentState.value.cameraPosition,
    ) {
        MapEffect(block = { map ->
            map.setMapStyle(style.value)
        })
        Polygon(
            points =
                LatLngConstants.northernHemisphere,
            holes =
                listOf(LatLngConstants.japanBoundsForPolygonHole),
            fillColor = Color.Black,
            strokeColor = Color.DarkGray,
            strokeJointType = JointType.ROUND,
            strokeWidth = 100f,
        )
    }
}
