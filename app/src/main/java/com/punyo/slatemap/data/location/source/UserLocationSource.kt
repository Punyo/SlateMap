package com.punyo.slatemap.data.location.source

import android.Manifest
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.punyo.slatemap.R
import com.punyo.slatemap.application.Regions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class UserLocationSource(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
) {
    private val onLocationUpdateCallbacks = mutableListOf<(Location) -> Unit>()
    private var currentMockingLocation: Location? = null
    private var currentMockingJob: Job? = null

    private val locationCallback: LocationCallback =
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                onLocationUpdateCallbacks.forEach { callback ->
                    locationResult.lastLocation?.let { callback(it) }
                }
            }
        }
    private val scope = CoroutineScope(Dispatchers.IO)

    suspend fun getLastLocation(): Result<Location> {
        if (currentMockingLocation != null) {
            return Result.success(currentMockingLocation!!)
        }
        return try {
            val location =
                suspendCancellableCoroutine { continuation ->
                    fusedLocationProviderClient.lastLocation
                        .addOnSuccessListener { location ->
                            continuation.resume(location)
                        }.addOnFailureListener { _ ->
                            continuation.resume(null)
                        }
                }
            if (location != null) {
                Result.success(location)
            } else {
                Result.failure(Exception("Location not available"))
            }
        } catch (e: SecurityException) {
            Result.failure(e)
        }
    }

    fun addLocationCallback(onLocationUpdate: (Location) -> Unit): Result<Nothing?> =
        try {
            if (onLocationUpdateCallbacks.isEmpty()) {
                startLocationCallback()
            }
            onLocationUpdateCallbacks.add(onLocationUpdate)
            Result.success(null)
        } catch (e: SecurityException) {
            Result.failure(e)
        }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun startLocationCallback() {
        val locationRequest =
            LocationRequest
                .Builder(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    LOCATION_UPDATE_INTERVAL,
                ).setWaitForAccurateLocation(true)
                .build()
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper(),
        )
    }

    fun setMockLocation(location: Location): Result<Nothing?> =
        try {
            currentMockingLocation = location
            if (currentMockingJob == null) {
                currentMockingJob =
                    scope.launch {
                        mockingLocationUpdate()
                    }
            }
            Result.success(null)
        } catch (e: SecurityException) {
            Result.failure(e)
        }

    private suspend fun CoroutineScope.mockingLocationUpdate() {
        while (isActive) {
            onLocationUpdateCallbacks.forEach { callback ->
                currentMockingLocation.let {
                    it?.let { callback(it) }
                }
            }
            delay(LOCATION_UPDATE_INTERVAL)
        }
    }

    fun clearMockLocation(): Result<Nothing?> =
        try {
            currentMockingJob?.cancel()
            currentMockingJob = null
            Result.success(null)
        } catch (e: SecurityException) {
            Result.failure(e)
        }

    suspend fun getRegionFromLocation(
        context: Context,
        location: Location,
    ): Regions {
        val hokkaido = context.resources.getStringArray(R.array.hokkaido)
        val tohoku = context.resources.getStringArray(R.array.tohoku)
        val kantoAndChubu = context.resources.getStringArray(R.array.kanto_and_chubu)
        val kinkiAndChugoku = context.resources.getStringArray(R.array.kinki_and_chugoku)
        val shikoku = context.resources.getStringArray(R.array.shikoku)
        val kyushuAndOkinawa = context.resources.getStringArray(R.array.kyushu_and_okinawa)
        val admin = getAddressFromLocation(context, location).adminArea
        val region =
            when {
                hokkaido.contains(admin) -> Regions.HOKKAIDO
                tohoku.contains(admin) -> Regions.TOHOKU
                kantoAndChubu.contains(admin) -> Regions.KANTO_AND_CHUBU
                kinkiAndChugoku.contains(admin) -> Regions.KINKI_AND_CHUGOKU
                shikoku.contains(admin) -> Regions.SHIKOKU
                kyushuAndOkinawa.contains(admin) -> Regions.KYUSHU_AND_OKINAWA
                else -> throw IllegalArgumentException("Unknown prefecture: $admin")
            }
        return region
    }

    @Suppress("DEPRECATION")
    suspend fun getAddressFromLocation(
        context: Context,
        location: Location,
    ): Address =
        suspendCancellableCoroutine { continuation ->
            val geocoder = Geocoder(context)
            if (Build.VERSION.SDK_INT < 33) {
                continuation.resume(
                    geocoder.getFromLocation(location.latitude, location.longitude, 1)!![0],
                )
            } else {
                geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1,
                ) { addresses ->
                    continuation.resume(addresses[0])
                }
            }
        }

    companion object {
        private const val LOCATION_UPDATE_INTERVAL = 1000L
    }
}
