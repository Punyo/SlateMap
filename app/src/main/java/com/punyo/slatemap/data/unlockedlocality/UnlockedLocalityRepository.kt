package com.punyo.slatemap.data.unlockedlocality

import com.punyo.slatemap.application.Regions
import com.punyo.slatemap.data.unlockedlocality.source.UnlockedLocalityEntity

interface UnlockedLocalityRepository {
    suspend fun deleteAllUnlockedLocalities()

    suspend fun getUnlockedLocalityByRegion(region: Regions): List<UnlockedLocalityEntity>

    suspend fun insertUnlockedLocality(unlockedLocality: UnlockedLocalityEntity)

    suspend fun isLocalityUnlocked(
        region: Regions,
        localityName: String,
    ): Boolean
}
