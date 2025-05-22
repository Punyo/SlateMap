package com.punyo.slatemap.data.poi

import android.graphics.Bitmap
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place

interface PoiRepository {
    suspend fun fetchPlaceDetails(placeId: String): Place

    suspend fun fetchPhoto(photoMetadata: PhotoMetadata): Bitmap
}
