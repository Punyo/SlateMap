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
        override suspend fun deleteAllUnlockedLocalities() = unlockedLocalitySource.deleteAllUnlockedLocalities()

        override suspend fun getUnlockedLocalityByRegion(region: Regions) = unlockedLocalitySource.getUnlockedLocalityByRegion(region)

        override suspend fun insertUnlockedLocality(
            localityName: String,
            unlockedDate: OffsetDateTime,
            region: Regions,
        ) {
            unlockedLocalitySource.insertUnlockedLocality(
                UnlockedLocalityEntity(
                    localityName = localityName,
                    unlockedDate = unlockedDate.toString(),
                    region = region.toString(),
                ),
            )
        }

        override suspend fun isLocalityUnlocked(
            region: Regions,
            localityName: String,
        ) = unlockedLocalitySource.isLocalityUnlocked(region, localityName)
    }
