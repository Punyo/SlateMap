package com.punyo.slatemap.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.location.Address
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Review
import com.google.maps.android.compose.CameraPositionState
import com.punyo.slatemap.application.Regions
import com.punyo.slatemap.data.location.LocationRepository
import com.punyo.slatemap.data.poi.PoiRepository
import com.punyo.slatemap.data.unlockedlocality.UnlockedLocalityRepository
import com.punyo.slatemap.data.unlockedlocality.source.UnlockedLocalityEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class MapScreenViewModel
    @Inject
    constructor(
        private val locationRepository: LocationRepository,
        private val unlockedLocalityRepository: UnlockedLocalityRepository,
        private val poiRepository: PoiRepository,
        private val applicationContext: Context,
    ) : ViewModel() {
        private val state = MutableStateFlow(MapScreenUiState())
        val uiState: StateFlow<MapScreenUiState> = state.asStateFlow()

        init {
            locationRepository
                .addLocationCallback(
                    onLocationUpdate = { location -> onLocationGetSuccess(location) },
                )
            viewModelScope.launch {
                state
                    .map { it.currentRegion }
                    .distinctUntilChanged()
                    .collectLatest { region ->
                        if (region != null) {
                            onRegionChanged(region)
                        }
                    }
            }
        }

        fun getUnlockedLocalitiesInCurrentRegion(): List<UnlockedLocalityEntity>? {
            val currentState = state.value
            return if (currentState.commitedUnlockedLocalitiesInCurrentRegion != null) {
                currentState.commitedUnlockedLocalitiesInCurrentRegion +
                    unlockedLocalityRepository
                        .getCurrentChanges()
                        .filter {
                            it.region == currentState.currentRegion.toString()
                        }
            } else {
                null
            }
        }

        private fun onRegionChanged(region: Regions) {
            val currentChanges =
                unlockedLocalityRepository.getCurrentChanges()

            viewModelScope.launch {
                if (currentChanges.isNotEmpty()) {
                    unlockedLocalityRepository.commitUnlockedLocalityChanges()
                }
                state.value =
                    state.value.copy(
                        commitedUnlockedLocalitiesInCurrentRegion =
                            unlockedLocalityRepository.getCommitedUnlockedLocalitiesByRegion(region),
                    )
            }
        }

        private fun onLocationGetSuccess(location: Location?) {
            viewModelScope.launch {
                location?.let {
                    val address =
                        locationRepository.getAddressFromLocation(
                            applicationContext,
                            it,
                        )
                    val region =
                        locationRepository.getRegionFromLocation(
                            location = it,
                            context = applicationContext,
                        )
                    val locality = state.value.currentAddress?.locality
                    if (address.locality != locality || locality == null) {
                        unlockedLocalityRepository.addUnlockedLocality(
                            localityName = address.locality,
                            unlockedDate = OffsetDateTime.now(),
                            region = region,
                        )
                    }

                    state.value =
                        state.value.copy(
                            currentLocation = location,
                            currentRegion = region,
                            currentAddress = address,
                        )
                }
            }
        }

        fun resetSelectedPoiPlaceId() {
            state.value =
                state.value.copy(
                    currentSelectedGooglePoiDetails = null,
                    isGooglePoiSelected = false,
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

        suspend fun getImageBitmapByPhotoMetadata(photoMetadata: PhotoMetadata): Bitmap = poiRepository.fetchPhoto(photoMetadata)

        fun onPoiClicked(
            googleMap: GoogleMap,
            poi: PointOfInterest,
        ) {
            val position = LatLng(poi.latLng.latitude, poi.latLng.longitude)
            googleMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition
                        .Builder()
                        .target(position)
                        .zoom(googleMap.cameraPosition.zoom)
                        .bearing(googleMap.cameraPosition.bearing)
                        .tilt(googleMap.cameraPosition.tilt)
                        .build(),
                ),
            )
            state.value =
                state.value.copy(
                    isGooglePoiSelected = true,
                )
            viewModelScope.launch {
                val place = poiRepository.fetchPlaceDetails(poi.placeId)
                state.value =
                    state.value.copy(
                        currentSelectedGooglePoiDetails =
                            GooglePoiDetails(
                                poi.placeId,
                                poi.name,
                                place.rating,
                                place.photoMetadatas,
                                place.reviews,
                            ),
                    )
            }
        }

        fun onPosSelected(latLng: LatLng) {
            state.value =
                state.value.copy(
                    isPosSelected = latLng,
                )
        }

        fun resetSelectedPos() {
            state.value =
                state.value.copy(
                    isPosSelected = null,
                )
        }

        fun addMarker(latLng: LatLng) {
            state.value =
                state.value.copy(
                    markers = state.value.markers + latLng,
                )
        }

        fun removeMarker(latLng: LatLng) {
            state.value =
                state.value.copy(
                    markers =
                        state.value.markers.filter {
                            it.latitude != latLng.latitude || it.longitude != latLng.longitude
                        },
                )
        }

        fun clearAllMarkers() {
            state.value =
                state.value.copy(
                    markers = emptyList(),
                )
        }
    }

data class MapScreenUiState(
    val currentLocation: Location? = null,
    val currentRegion: Regions? = null,
    val currentAddress: Address? = null,
    val currentSelectedGooglePoiDetails: GooglePoiDetails? = null,
    val isGooglePoiSelected: Boolean = false,
    val isPosSelected: LatLng? = null,
    val commitedUnlockedLocalitiesInCurrentRegion: List<UnlockedLocalityEntity>? = null,
    val markers: List<LatLng> = emptyList(),
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

data class GooglePoiDetails(
    val placeId: String,
    val name: String,
    val rating: Double? = null,
    val photoMetadata: List<PhotoMetadata>? = null,
    val googleReviews: List<Review>? = null,
)
