package com.punyo.slatemap.ui.map

import android.location.Location
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.punyo.slatemap.data.location.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MapScreenViewModel
    @Inject
    constructor(
        locationRepository: LocationRepository,
    ) : ViewModel() {
        private val state = MutableStateFlow(MapScreenUiState())
        val uiState: StateFlow<MapScreenUiState> = state.asStateFlow()

        init {
            locationRepository
                .getLastLocation()
                .onSuccess { it ->
                    it.addOnSuccessListener { onLocationGetSuccess(it) }
                }
        }

        private fun onLocationGetSuccess(location: Location?) {
            state.value =
                state.value.copy(
                    currentLocation = location,
                    cameraPosition = getCameraPositionState(location),
                )
        }

        private fun getCameraPositionState(location: Location?) =
            CameraPositionState(
                position =
                    CameraPosition.fromLatLngZoom(
                        LatLng(
                            location?.latitude ?: 0.0,
                            location?.longitude ?: 0.0,
                        ),
                        7f,
                    ),
            )
    }

data class MapScreenUiState(
    val currentLocation: Location? = null,
    val cameraPosition: CameraPositionState =
        CameraPositionState(
            position =
                CameraPosition.fromLatLngZoom(
                    LatLng(
                        35.6812,
                        139.7671,
                    ),
                    10f,
                ),
        ),
)
