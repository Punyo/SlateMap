package com.punyo.slatemap.data.location.source

import com.google.android.gms.location.FusedLocationProviderClient

class UserLocationSource(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
) {
    fun getLastLocation() = fusedLocationProviderClient.lastLocation
}
