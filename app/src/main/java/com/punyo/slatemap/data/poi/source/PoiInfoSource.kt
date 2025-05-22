package com.punyo.slatemap.data.poi.source

import android.graphics.Bitmap
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PoiInfoSource(
    private val placesClient: PlacesClient,
) {
    private val placeFields =
        listOf(
            Place.Field.RATING,
            Place.Field.PHOTO_METADATAS,
            Place.Field.REVIEWS,
        )

    suspend fun fetchPlaceDetails(placeId: String): Place =
        suspendCoroutine { continuation ->
            val request = FetchPlaceRequest.newInstance(placeId, placeFields)

            val placeTask: Task<FetchPlaceResponse> = placesClient.fetchPlace(request)

            placeTask
                .addOnSuccessListener { response ->
                    continuation.resume(response.place)
                }.addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }

    suspend fun fetchPhoto(photoMetadata: PhotoMetadata): Bitmap =
        suspendCoroutine { continuation ->
            val photoRequest =
                FetchPhotoRequest
                    .builder(photoMetadata)
                    .build()

            val photoTask: Task<FetchPhotoResponse> = placesClient.fetchPhoto(photoRequest)

            photoTask
                .addOnSuccessListener { response ->
                    continuation.resume(response.bitmap)
                }.addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
}
