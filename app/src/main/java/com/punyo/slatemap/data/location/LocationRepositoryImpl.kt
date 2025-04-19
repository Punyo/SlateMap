package com.punyo.slatemap.data.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import com.punyo.slatemap.data.location.source.UnlockedLocationSource
import com.punyo.slatemap.data.location.source.UserLocationSource
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class LocationRepositoryImpl
    @Inject
    constructor() : LocationRepository {
        @Inject
        lateinit var userLocationSource: UserLocationSource

        @Inject
        lateinit var unlockedLocationSource: UnlockedLocationSource

        override fun getLastLocation() = userLocationSource.getLastLocation()

        override fun setMockLocation(location: Location) = userLocationSource.setMockLocation(location)

        override fun clearMockLocation() = userLocationSource.clearMockLocation()

        @Suppress("DEPRECATION")
        override suspend fun getAddressFromLocation(
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
                    geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                        continuation.resume(addresses[0])
                    }
                }
            }
    }
