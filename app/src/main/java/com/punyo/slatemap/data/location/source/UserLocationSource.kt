package com.punyo.slatemap.data.location.source

import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Task

class UserLocationSource(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
) {
    fun getLastLocation(): Result<Task<Location>> =
        try {
            Result.success(fusedLocationProviderClient.lastLocation)
        } catch (e: SecurityException) {
            Result.failure(e)
        }

    fun setMockLocation(location: Location): Result<Nothing?> =
        try {
            fusedLocationProviderClient.setMockMode(true)
            fusedLocationProviderClient.setMockLocation(location)
            Result.success(null)
        } catch (e: SecurityException) {
            Result.failure(e)
        }

    fun clearMockLocation(): Result<Nothing?> =
        try {
            fusedLocationProviderClient.setMockMode(false)
            Result.success(null)
        } catch (e: SecurityException) {
            Result.failure(e)
        }
}
