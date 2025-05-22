package com.punyo.slatemap.data.location

import android.content.Context
import android.location.Address
import android.location.Location
import com.punyo.slatemap.data.location.source.UserLocationSource
import javax.inject.Inject

class LocationRepositoryImpl
    @Inject
    constructor() : LocationRepository {
        @Inject
        lateinit var userLocationSource: UserLocationSource

        override suspend fun getLastLocation() = userLocationSource.getLastLocation()

        override fun addLocationCallback(onLocationUpdate: (Location) -> Unit) = userLocationSource.addLocationCallback(onLocationUpdate)

        override fun setMockLocation(location: Location) = userLocationSource.setMockLocation(location)

        override fun clearMockLocation() = userLocationSource.clearMockLocation()

        override suspend fun getAddressFromLocation(
            context: Context,
            location: Location,
        ): Address = userLocationSource.getAddressFromLocation(context, location)

        override suspend fun getRegionFromLocation(
            context: Context,
            location: Location,
        ) = userLocationSource.getRegionFromLocation(context, location)
    }
