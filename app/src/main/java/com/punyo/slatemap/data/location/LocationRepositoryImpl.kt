package com.punyo.slatemap.data.location

import com.punyo.slatemap.data.location.source.UserLocationSource
import javax.inject.Inject

class LocationRepositoryImpl
    @Inject
    constructor() : LocationRepository {
        @Inject
        lateinit var userLocationSource: UserLocationSource

        override fun getLastLocation() = userLocationSource.getLastLocation()
    }
