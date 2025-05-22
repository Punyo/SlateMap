package com.punyo.slatemap.ui.debug

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.punyo.slatemap.data.location.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebugScreenViewModel
    @Inject
    constructor(
        application: Application,
        private val locationRepository: LocationRepository,
    ) : AndroidViewModel(application) {
        init {
            viewModelScope.launch {
                updateLocation()
            }
        }

        private val state = MutableStateFlow(DebugScreenUiState())
        private val context by lazy { getApplication<Application>().applicationContext!! }
        private val scope by lazy { CoroutineScope(Dispatchers.IO) }
        val uiState: StateFlow<DebugScreenUiState> = state.asStateFlow()

        private fun onLocationGetSuccess(location: Location?) {
            scope.launch {
                val address =
                    location?.let { locationRepository.getAddressFromLocation(context, it) }
                state.value = state.value.copy(currentGeoInfoString = address.toString())
            }
        }

        suspend fun updateLocation() {
            locationRepository
                .getLastLocation()
                .onSuccess {
                    onLocationGetSuccess(it)
                }.onFailure {
                    state.value =
                        state.value.copy(currentGeoInfoString = "位置情報の取得に失敗しました")
                }
        }

        fun clearMockLocation() {
            locationRepository
                .clearMockLocation()
                .onSuccess {
                    viewModelScope.launch {
                        updateLocation()
                    }
                }
        }

        fun setMockLocation(location: Location) {
            locationRepository
                .setMockLocation(location)
                .onSuccess {
                    viewModelScope.launch {
                        updateLocation()
                    }
                }
        }
    }

data class DebugScreenUiState(
    val currentGeoInfoString: String = "",
)
