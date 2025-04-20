package com.punyo.slatemap.ui.map

import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.punyo.slatemap.application.Regions
import com.punyo.slatemap.data.location.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapScreenViewModel
    @Inject
    constructor(
        private val locationRepository: LocationRepository,
        private val applicationContext: Context,
    ) : ViewModel() {
        private val state = MutableStateFlow(MapScreenUiState())
        val uiState: StateFlow<MapScreenUiState> = state.asStateFlow()

        init {
            locationRepository
                .addLocationCallback(
                    onLocationUpdate = { location -> onLocationGetSuccess(location) },
                )
        }

        private fun onLocationGetSuccess(location: Location?) {
            viewModelScope.launch {
                val address =
                    location?.let { locationRepository.getAddressFromLocation(applicationContext, it) }
                val region =
                    location?.let {
                        locationRepository.getRegionFromLocation(
                            location = it,
                            context = applicationContext,
                        )
                    }
                state.value =
                    state.value.copy(
                        currentLocation = location,
                        currentRegion = region,
                        currentLocalityName = address?.locality,
                    )
            }
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
    val currentRegion: Regions? = null,
    val currentLocalityName: String? = null,
    val unlockedLocationsIdInCurrentRegion: List<Int>? = null,
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
