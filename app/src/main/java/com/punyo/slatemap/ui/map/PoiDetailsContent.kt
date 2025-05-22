package com.punyo.slatemap.ui.map

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.google.android.libraries.places.api.model.AuthorAttribution
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Review
import com.punyo.slatemap.R

private const val MAX_LINES = 3

@Composable
private fun PhotoPlaceholder(
    it: PhotoMetadata,
    onLoadBitmapByPhotoMetadata: suspend (PhotoMetadata) -> Bitmap,
) {
    val imageBitmap = remember { mutableStateOf<Bitmap?>(null) }
    val isLoading = remember { mutableStateOf(true) }
    val latestOnLoadBitmapByPhotoMetadata by rememberUpdatedState(onLoadBitmapByPhotoMetadata)
    LaunchedEffect(it) {
        isLoading.value = true
        imageBitmap.value =
            latestOnLoadBitmapByPhotoMetadata(it)
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
}

@Composable
fun PoiDetailContent(
    name: String,
    place: PoiDetails,
    onLoadBitmapByPhotoMetadata: suspend (PhotoMetadata) -> Bitmap,
    modifier: Modifier = Modifier,
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
                        PhotoPlaceholder(
                            it,
                            onLoadBitmapByPhotoMetadata =
                            onLoadBitmapByPhotoMetadata,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // レビュー欄
        place.googleReviews?.let {
            ReviewsSection(
                reviews = it,
            )
        }
    }
}

@Composable
fun ReviewsSection(
    modifier: Modifier = Modifier,
    reviews: List<Review> = emptyList(),
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
    ) {
        Text(
            text = "レビュー",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        reviews.forEachIndexed { index, it ->
            ReviewItem(
                review = it,
            )
            if (index != reviews.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = Color.LightGray,
                )
            }
        }
    }
}

@Composable
fun ReviewItem(
    review: Review,
    modifier: Modifier = Modifier,
) {
    val textStyle = MaterialTheme.typography.bodyMedium
    val isExpanded = remember { mutableStateOf(false) }
    val textMeasurer = rememberTextMeasurer()

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
    ) {
        // 著者情報と評価
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            // 著者名
            Text(
                text =
                    buildAnnotatedString {
                        if (review.authorAttribution.uri != null) {
                            withLink(
                                LinkAnnotation.Url(review.authorAttribution.uri!!),
                            ) {
                                append(review.authorAttribution.name)
                            }
                        } else {
                            append(review.authorAttribution.name)
                        }
                    },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
            )

            // 評価
            Text(
                text = "★ ${review.rating}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        // 投稿日時
        review.publishTime?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 2.dp),
            )
        }

        review.text?.let { reviewText ->
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val textLayoutResult =
                    remember(review.text, textMeasurer) {
                        val text = review.text ?: ""
                        textMeasurer.measure(
                            text = text,
                            style = textStyle,
                            constraints = constraints,
                        )
                    }
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (textLayoutResult.lineCount > MAX_LINES) {
                        // 行数が閾値を超える場合
                        if (isExpanded.value) {
                            // 展開状態：全テキスト表示
                            Text(
                                text = reviewText,
                                style = textStyle,
                                modifier = Modifier.padding(top = 4.dp),
                            )

                            // 「閉じる」ボタン
                            TextButton(
                                onClick = { isExpanded.value = false },
                                modifier = Modifier.align(Alignment.End),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    text = "閉じる",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        } else {
                            // 折りたたみ状態：maxLinesまでのテキスト表示
                            val endIndex =
                                textLayoutResult.getLineEnd(MAX_LINES - 1, visibleEnd = true)
                            val truncatedText = reviewText.substring(0, endIndex)

                            Text(
                                text = truncatedText,
                                style = textStyle,
                                modifier = Modifier.padding(top = 4.dp),
                                maxLines = MAX_LINES,
                                overflow = TextOverflow.Ellipsis,
                            )

                            // 「もっと見る」ボタン
                            TextButton(
                                onClick = { isExpanded.value = true },
                                modifier = Modifier.align(Alignment.End),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    text = "もっと見る",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    } else {
                        // 行数が閾値以下の場合：全テキスト表示
                        Text(
                            text = reviewText,
                            style = textStyle,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PoiDetailContentPreview() {
    val context = LocalContext.current
    val photoMetadata =
        PhotoMetadata.builder("A").build()
    val mockReview =
        Review
            .builder(1.2, AuthorAttribution.builder("A").build())
            .setText("これはレビューのテキストです。")
            .setPublishTime("2023-10-01T12:00:00Z")
            .build()
    val longMockReview =
        Review
            .builder(1.2, AuthorAttribution.builder("A").build())
            .setText(
                "これはながあああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ\nあああああああああああああああああああああああああああああああああああああああああああああああああああああああああああいレビューのテキストです。",
            ).setPublishTime("2023-10-01T12:00:00Z")
            .build()
    MaterialTheme {
        MaterialTheme {
            val mockPoiDetails =
                PoiDetails(
                    placeId = "ChIJaXQRs6lZwokRY6EFpJnhNNE",
                    name = "エンパイア ステート ビル",
                    rating = 4.5,
                    photoMetadata = listOf(photoMetadata, photoMetadata),
                    googleReviews = listOf(mockReview, longMockReview, mockReview),
                )

            PoiDetailContent(
                name = mockPoiDetails.name,
                place = mockPoiDetails,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                onLoadBitmapByPhotoMetadata = {
                    return@PoiDetailContent context
                        .getDrawable(R.drawable.ic_launcher_background)
                        ?.toBitmap()!!
                },
            )
        }
    }
}
