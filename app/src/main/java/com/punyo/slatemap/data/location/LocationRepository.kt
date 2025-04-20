package com.punyo.slatemap.data.location

import android.content.Context
import android.location.Address
import android.location.Location
import com.google.android.gms.tasks.Task
import com.punyo.slatemap.application.Regions

interface LocationRepository {
    fun getLastLocation(): Result<Task<Location>>

    fun addLocationCallback(onLocationUpdate: (Location) -> Unit): Result<Nothing?>

    fun setMockLocation(location: Location): Result<Nothing?>

    fun clearMockLocation(): Result<Nothing?>

    suspend fun getAddressFromLocation(
        context: Context,
        location: Location,
    ): Address

    suspend fun getRegionFromLocation(
        context: Context,
        location: Location,
    ): Regions
}
