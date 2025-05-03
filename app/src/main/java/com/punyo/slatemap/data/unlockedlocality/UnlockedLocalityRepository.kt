package com.punyo.slatemap.data.unlockedlocality

import com.punyo.slatemap.application.Regions
import com.punyo.slatemap.data.unlockedlocality.source.UnlockedLocalityEntity
import java.time.OffsetDateTime

interface UnlockedLocalityRepository {
    suspend fun deleteAllUnlockedLocalities()

    suspend fun getUnlockedLocalityByRegion(region: Regions): List<UnlockedLocalityEntity>

    suspend fun getCommitedUnlockedLocalitiesByRegion(region: Regions): List<UnlockedLocalityEntity>

    suspend fun addUnlockedLocality(
        localityName: String,
        unlockedDate: OffsetDateTime,
        region: Regions,
    )

    suspend fun commitUnlockedLocalityChanges()

    suspend fun isLocalityUnlocked(
        region: Regions,
        localityName: String,
    ): Boolean

    fun getCurrentChanges(): List<UnlockedLocalityEntity>
}
