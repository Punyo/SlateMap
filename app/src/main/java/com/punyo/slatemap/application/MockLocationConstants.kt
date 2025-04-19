package com.punyo.slatemap.application

import android.location.Location

object MockLocationConstants {
    val tokyo =
        Location(null).apply {
            latitude = 35.6811
            longitude = 139.7669
            accuracy = 10f
        }
    val nagoya =
        Location(null).apply {
            latitude = 35.1700
            longitude = 136.8837
            accuracy = 10f
        }
    val osaka =
        Location(null).apply {
            latitude = 34.7025
            longitude = 135.4959
            accuracy = 10f
        }
}
