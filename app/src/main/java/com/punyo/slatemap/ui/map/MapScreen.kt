package com.punyo.slatemap.ui.map

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
import com.punyo.slatemap.R
import com.punyo.slatemap.application.GeoJsonLayerGenerator
import com.punyo.slatemap.application.constant.LatLngConstants

@OptIn(MapsComposeExperimentalApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    mapScreenViewModel: MapScreenViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val currentState by mapScreenViewModel.uiState.collectAsStateWithLifecycle()
    val style =
        remember { mutableStateOf(MapStyleOptions.loadRawResourceStyle(context, R.raw.style)) }
    val bottomSheetState =
        rememberModalBottomSheetState()
    if (currentState.currentLocation != null &&
        currentState.currentRegion != null &&
        currentState.currentAddress != null &&
        currentState.commitedUnlockedLocalitiesInCurrentRegion != null
    ) {
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
                    indoorLevelPickerEnabled = true,
                ),
            cameraPositionState = currentState.cameraPosition,
        ) {
            MapEffect(block = { map ->
                map.isMyLocationEnabled = true
                map.setMapStyle(style.value)
                map.setOnPoiClickListener {
                    mapScreenViewModel.onPoiClicked(map, it)
                }
                GeoJsonLayerGenerator(
                    map = map,
                    currentUserRegion = currentState.currentRegion!!,
                ).generateGeoJsonLayer(
                    context = context,
                    unlockedLocalityInRegion =
                        mapScreenViewModel
                            .getUnlockedLocalitiesInCurrentRegion()!!
                            .map {
                                it.localityName
                            },
                )
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
            if (currentState.isPoiSelected) {
                ModalBottomSheet(
                    onDismissRequest = {
                        mapScreenViewModel.resetSelectedPoiPlaceId()
                    },
                    sheetState = bottomSheetState,
                ) {
                    if (currentState.currentSelectedPoiDetails != null) {
                        PoiDetailContent(
                            name = currentState.currentSelectedPoiDetails!!.name,
                            place = currentState.currentSelectedPoiDetails!!,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                            onLoadBitmapByPhotoMetadata =
                                mapScreenViewModel::getImageBitmapByPhotoMetadata,
                        )
                    } else {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    } else {
        CircularProgressIndicator(
            modifier = modifier,
        )
    }
}
