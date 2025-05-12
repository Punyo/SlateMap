package com.punyo.slatemap.ui.map

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Polygon
import com.punyo.slatemap.R
import com.punyo.slatemap.application.GeoJsonLayerGenerator
import com.punyo.slatemap.application.constant.LatLngConstants

@OptIn(MapsComposeExperimentalApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    mapScreenViewModel: MapScreenViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val currentState by mapScreenViewModel.uiState.collectAsStateWithLifecycle()
    val style =
        remember { mutableStateOf(MapStyleOptions.loadRawResourceStyle(context, R.raw.style)) }
    val bottomSheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (currentState.currentLocation != null &&
        currentState.currentRegion != null &&
        currentState.currentAddress != null &&
        currentState.commitedUnlockedLocalitiesInCurrentRegion != null
    ) {
        GoogleMap(
            modifier = modifier,
            properties =
                MapProperties(
                    minZoomPreference = 5.5f,
                    latLngBoundsForCameraTarget = LatLngConstants.japanBoundsForCameraTarget,
                ),
            uiSettings =
                MapUiSettings(
                    compassEnabled = true,
                    mapToolbarEnabled = true,
                    myLocationButtonEnabled = true,
                    indoorLevelPickerEnabled = true,
                ),
            cameraPositionState = currentState.cameraPosition,
        ) {
            MapEffect(block = { map ->
                map.isMyLocationEnabled = true
                map.setMapStyle(style.value)
                map.setOnPoiClickListener {
                    mapScreenViewModel.onPoiClicked(map, it)
                }
                GeoJsonLayerGenerator(
                    map = map,
                    currentUserRegion = currentState.currentRegion!!,
                ).generateGeoJsonLayer(
                    context = context,
                    unlockedLocalityInRegion =
                        mapScreenViewModel
                            .getUnlockedLocalitiesInCurrentRegion()!!
                            .map {
                                it.localityName
                            },
                )
            })
            Polygon(
                points =
                    LatLngConstants.northernHemisphere,
                holes =
                    listOf(LatLngConstants.japanBoundsForPolygonHole),
                fillColor = Color.Black,
                strokeColor = Color.DarkGray,
                strokeJointType = JointType.BEVEL,
                strokeWidth = 50f,
            )
            if (currentState.isPoiSelected) {
                ModalBottomSheet(
                    onDismissRequest = {
                        mapScreenViewModel.resetSelectedPoiPlaceId()
                    },
                    sheetState = bottomSheetState,
                ) {
                    if (currentState.currentSelectedPoiDetails != null) {
                        PoiDetailContent(
                            name = currentState.currentSelectedPoiDetails!!.name,
                            place = currentState.currentSelectedPoiDetails!!,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                            photoComposable = {
                                val imageBitmap = remember { mutableStateOf<Bitmap?>(null) }
                                val isLoading = remember { mutableStateOf(true) }
                                LaunchedEffect(it) {
                                    isLoading.value = true
                                    try {
                                        imageBitmap.value =
                                            mapScreenViewModel.getImageBitmapByPhotoMetadata(it)
                                    } catch (e: Exception) {
                                        // エラー処理が必要な場合はここに追加
                                    } finally {
                                        isLoading.value = false
                                    }
                                }

                                if (isLoading.value) {
                                    Box(
                                        modifier = Modifier.size(width = 240.dp, height = 160.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                } else {
                                    imageBitmap.value?.let { bitmap ->
                                        Image(
                                            bitmap = bitmap.asImageBitmap(),
                                            contentDescription = null,
                                            modifier =
                                                Modifier
                                                    .size(width = 240.dp, height = 160.dp)
                                                    .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop,
                                        )
                                    }
                                }
                            },
                        )
                    } else {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    } else {
        CircularProgressIndicator(
            modifier = modifier,
        )
    }
}

@Composable
fun PoiDetailContent(
    name: String,
    place: PoiDetails,
    modifier: Modifier = Modifier,
    photoComposable: @Composable (PhotoMetadata) -> Unit = {},
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
    ) {
        // 名前
        Text(
            text = name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp),
        )

        // 評価
        place.rating?.let { rate ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp),
            ) {
                Text(
                    text = "Googleマップでの評価：$rate",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        if (place.photoMetadata != null) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp),
            ) {
                place.photoMetadata.forEach {
                    item {
                        photoComposable(it)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun PoiDetailContentPreview() {
    val context = LocalContext.current
    val photoMetadata =
        PhotoMetadata.builder("A").build()
//    val review = Review.builder(1.0, AuthorAttribution.builder("a").build()).build()
    MaterialTheme {
        val mockPoiDetails =
            PoiDetails(
                placeId = "ChIJaXQRs6lZwokRY6EFpJnhNNE",
                name = "エンパイア ステート ビル",
                rating = 4.5,
                photoMetadata = listOf(photoMetadata, photoMetadata),
            )

        PoiDetailContent(
            name = mockPoiDetails.name,
            place = mockPoiDetails,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            photoComposable = {
                Image(
                    bitmap =
                        context
                            .getDrawable(R.drawable.ic_launcher_background)
                            ?.toBitmap()!!
                            .asImageBitmap(),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .size(width = 240.dp, height = 160.dp)
                            .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                )
            },
        )
    }
}
