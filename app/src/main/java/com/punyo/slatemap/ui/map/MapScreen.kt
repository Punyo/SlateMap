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
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Polygon
import com.google.maps.android.data.geojson.GeoJsonLayer
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
        uiSettings =
            MapUiSettings(
                compassEnabled = true,
                mapToolbarEnabled = true,
                myLocationButtonEnabled = true,
            ),
        cameraPositionState = currentState.value.cameraPosition,
    ) {
        MapEffect(block = { map ->
            map.setMapStyle(style.value)
            val layer =
                GeoJsonLayer(map, R.raw.kanto_and_chubu, context)
            layer.features
                .filter { feature ->
                    feature.getProperty("N03_003") == "名古屋市"
                }.forEach { layer.removeFeature(it) }
            layer.defaultPolygonStyle.apply {
                fillColor = 0xff000000.toInt()
                strokeColor = 0xff000064.toInt()
                strokeWidth = 5f
                strokeJointType = JointType.BEVEL
            }
            layer.setOnFeatureClickListener { feature ->
                feature.geometry
            }
            layer.addLayerToMap()
        })
        Polygon(
            points =
                LatLngConstants.northernHemisphere,
            holes =
                listOf(LatLngConstants.japanBoundsForPolygonHole),
            fillColor = Color.Black,
            strokeColor = Color.DarkGray,
            strokeJointType = JointType.BEVEL,
            strokeWidth = 50f,
        )
    }
}
