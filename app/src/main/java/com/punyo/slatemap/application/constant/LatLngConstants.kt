package com.punyo.slatemap.application.constant

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

object LatLngConstants {
    val northernHemisphere =
        listOf(
            LatLng(0.0, 0.0),
            LatLng(0.0, 179.9),
            LatLng(179.9, 179.9),
            LatLng(179.9, 0.0),
            LatLng(0.0, 0.0),
        )

    val japanBoundsForPolygonHole =
        listOf(
            LatLng(20.2531, 122.5557),
            LatLng(20.2531, 153.5912),
            LatLng(45.8026, 153.5912),
            LatLng(45.8026, 140.5475),
            LatLng(45.8026, 140.5475),
            LatLng(37.4306, 131.3699),
            LatLng(34.7760, 129.2202),
            LatLng(32.7421, 127.4104),
            LatLng(26.4321, 122.6749),
            LatLng(20.2531, 122.5557),
        )

    val japanBoundsForCameraTarget =
        LatLngBounds(
            LatLng(
                20.2531,
                122.5557,
            ),
            LatLng(
                45.8026,
                153.5912,
            ),
        )
}
