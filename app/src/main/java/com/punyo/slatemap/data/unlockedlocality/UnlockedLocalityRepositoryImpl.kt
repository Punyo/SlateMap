package com.punyo.slatemap.data.unlockedlocality

import com.punyo.slatemap.application.Regions
import com.punyo.slatemap.data.unlockedlocality.source.UnlockedLocalityEntity
import com.punyo.slatemap.data.unlockedlocality.source.UnlockedLocalitySource
import java.time.OffsetDateTime
import javax.inject.Inject

class UnlockedLocalityRepositoryImpl
    @Inject
    constructor(
        private val unlockedLocalitySource: UnlockedLocalitySource,
    ) : UnlockedLocalityRepository {
        private val currentUnlockedLocalityChanges: MutableList<UnlockedLocalityEntity> =
            mutableListOf()

        override suspend fun deleteAllUnlockedLocalities() = unlockedLocalitySource.deleteAllUnlockedLocalities()

        override suspend fun getUnlockedLocalityByRegion(region: Regions) = unlockedLocalitySource.getUnlockedLocalityByRegion(region)

        override suspend fun getCommitedUnlockedLocalitiesByRegion(region: Regions): List<UnlockedLocalityEntity> =
            unlockedLocalitySource.getUnlockedLocalityByRegion(region)

        override suspend fun addUnlockedLocality(
            localityName: String,
            unlockedDate: OffsetDateTime,
            region: Regions,
        ) {
            currentUnlockedLocalityChanges.add(
                UnlockedLocalityEntity(
                    localityName = localityName,
                    unlockedDate = unlockedDate.toString(),
                    region = region.toString(),
                ),
            )
        }

        override suspend fun commitUnlockedLocalityChanges() {
            currentUnlockedLocalityChanges.forEach { unlockedLocality ->
                unlockedLocalitySource.insertUnlockedLocality(unlockedLocality)
            }
            currentUnlockedLocalityChanges.clear()
        }

        override suspend fun isLocalityUnlocked(
            region: Regions,
            localityName: String,
        ) = unlockedLocalitySource.isLocalityUnlocked(region, localityName)

        override fun getCurrentChanges(): List<UnlockedLocalityEntity> = currentUnlockedLocalityChanges
    }
