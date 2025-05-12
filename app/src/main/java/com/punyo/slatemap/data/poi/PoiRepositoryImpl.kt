package com.punyo.slatemap.data.poi

import android.graphics.Bitmap
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.punyo.slatemap.data.poi.source.PoiInfoSource
import javax.inject.Inject

class PoiRepositoryImpl
    @Inject
    constructor(
        private val poiInfoSource: PoiInfoSource,
    ) : PoiRepository {
        override suspend fun fetchPlaceDetails(placeId: String): Place = poiInfoSource.fetchPlaceDetails(placeId)

        override suspend fun fetchPhoto(photoMetadata: PhotoMetadata): Bitmap = poiInfoSource.fetchPhoto(photoMetadata)
    }
