package com.punyo.slatemap.application

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.JointType
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.punyo.slatemap.R

class GeoJsonLayerGenerator(
    private val map: GoogleMap,
    private val currentUserRegion: Regions,
) {
    fun generateGeoJsonLayer(context: Context) {
        for (region in Regions.entries) {
            if (region != currentUserRegion) {
                val layer =
                    GeoJsonLayer(map, getCombinedGeoJsonResourceIdFromRegion(region), context)
                layer.defaultPolygonStyle.apply {
                    fillColor = context.getColor(R.color.geoJsonLayer_combined_fillColor)
                    strokeColor = context.getColor(R.color.geoJsonLayer_combined_strokeColor)
                    strokeWidth = 7.5f
                    strokeJointType = JointType.BEVEL
                }
                layer.addLayerToMap()
            } else {
                val layer =
                    GeoJsonLayer(map, getGeoJsonResourceIdFromRegion(region), context)
                layer.defaultPolygonStyle.apply {
                    fillColor = context.getColor(R.color.geoJsonLayer_fillColor)
                    strokeColor = context.getColor(R.color.geoJsonLayer_strokeColor)
                    strokeWidth = 5f
                    strokeJointType = JointType.BEVEL
                }
                layer.addLayerToMap()
            }
        }
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
