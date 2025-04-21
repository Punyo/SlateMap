package com.punyo.slatemap.application

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.JointType
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.punyo.slatemap.R
import com.punyo.slatemap.application.constant.GeoJsonPropertyNameConstants

class GeoJsonLayerGenerator(
    private val map: GoogleMap,
    private val currentUserRegion: Regions,
) {
    private var currentHokkaidoLayer: GeoJsonLayer? = null
    private var currentTohokuLayer: GeoJsonLayer? = null
    private var currentKantoAndChubuLayer: GeoJsonLayer? = null
    private var currentKinkiAndChugokuLayer: GeoJsonLayer? = null
    private var currentShikokuLayer: GeoJsonLayer? = null
    private var currentKyushuAndOkinawaLayer: GeoJsonLayer? = null

    fun generateGeoJsonLayer(
        context: Context,
        unlockedLocalityInRegion: List<String>,
    ) {
        for (region in Regions.entries) {
            val layer: GeoJsonLayer
            if (region != currentUserRegion) {
                layer = GeoJsonLayer(map, getCombinedGeoJsonResourceIdFromRegion(region), context)
                layer.defaultPolygonStyle.apply {
                    fillColor = context.getColor(R.color.geoJsonLayer_combined_fillColor)
                    strokeColor = context.getColor(R.color.geoJsonLayer_combined_strokeColor)
                    strokeWidth = 7.5f
                    strokeJointType = JointType.BEVEL
                }
            } else {
                layer = GeoJsonLayer(map, getGeoJsonResourceIdFromRegion(region), context)
                layer.defaultPolygonStyle.apply {
                    fillColor = context.getColor(R.color.geoJsonLayer_fillColor)
                    strokeColor = context.getColor(R.color.geoJsonLayer_strokeColor)
                    strokeWidth = 5f
                    strokeJointType = JointType.BEVEL
                }
                unlockedLocalityInRegion.forEach { localityName ->
                    layer.removeFeature(
                        layer.features.filter {
                            it.getProperty(
                                GeoJsonPropertyNameConstants.CITY_OR_TOKYO_SPECIAL_WARD,
                            ) == localityName
                        }[0],
                    )
                }
            }
            setGeoJsonLayer(layer, region)
        }
    }

    private fun setGeoJsonLayer(
        layer: GeoJsonLayer,
        region: Regions,
    ) {
        when (region) {
            Regions.HOKKAIDO -> currentHokkaidoLayer = layer
            Regions.TOHOKU -> currentTohokuLayer = layer
            Regions.KANTO_AND_CHUBU -> currentKantoAndChubuLayer = layer
            Regions.KINKI_AND_CHUGOKU -> currentKinkiAndChugokuLayer = layer
            Regions.SHIKOKU -> currentShikokuLayer = layer
            Regions.KYUSHU_AND_OKINAWA -> currentKyushuAndOkinawaLayer = layer
        }
        layer.addLayerToMap()
    }

    fun getGeoJsonLayer(region: Regions): GeoJsonLayer? =
        when (region) {
            Regions.HOKKAIDO -> currentHokkaidoLayer
            Regions.TOHOKU -> currentTohokuLayer
            Regions.KANTO_AND_CHUBU -> currentKantoAndChubuLayer
            Regions.KINKI_AND_CHUGOKU -> currentKinkiAndChugokuLayer
            Regions.SHIKOKU -> currentShikokuLayer
            Regions.KYUSHU_AND_OKINAWA -> currentKyushuAndOkinawaLayer
        }

    private fun getGeoJsonResourceIdFromRegion(region: Regions): Int =
        when (region) {
            Regions.HOKKAIDO -> R.raw.hokkaido
            Regions.TOHOKU -> R.raw.tohoku
            Regions.KANTO_AND_CHUBU -> R.raw.kanto_and_chubu
            Regions.KINKI_AND_CHUGOKU -> R.raw.kinki_and_chugoku
            Regions.SHIKOKU -> R.raw.shikoku
            Regions.KYUSHU_AND_OKINAWA -> R.raw.kyushu_and_okinawa
        }

    private fun getCombinedGeoJsonResourceIdFromRegion(region: Regions): Int =
        when (region) {
            Regions.HOKKAIDO -> R.raw.hokkaido_combined
            Regions.TOHOKU -> R.raw.tohoku_combined
            Regions.KANTO_AND_CHUBU -> R.raw.kanto_and_chubu_combined
            Regions.KINKI_AND_CHUGOKU -> R.raw.kinki_and_chugoku_combined
            Regions.SHIKOKU -> R.raw.shikoku_combined
            Regions.KYUSHU_AND_OKINAWA -> R.raw.kyushu_and_okinawa_combined
        }
}
