package com.punyo.slatemap.data.unlockedlocality

import com.punyo.slatemap.application.Regions
import com.punyo.slatemap.data.unlockedlocality.source.UnlockedLocalityEntity
import com.punyo.slatemap.data.unlockedlocality.source.UnlockedLocalitySource
import javax.inject.Inject

class UnlockedLocalityRepositoryImpl
    @Inject
    constructor(
        private val unlockedLocalitySource: UnlockedLocalitySource,
    ) : UnlockedLocalityRepository {
        override suspend fun deleteAllUnlockedLocalities() = unlockedLocalitySource.deleteAllUnlockedLocalities()

        override suspend fun getUnlockedLocalityByRegion(region: Regions) = unlockedLocalitySource.getUnlockedLocalityByRegion(region)

        override suspend fun insertUnlockedLocality(unlockedLocality: UnlockedLocalityEntity) =
            unlockedLocalitySource.insertUnlockedLocality(unlockedLocality)

        override suspend fun isLocalityUnlocked(
            region: Regions,
            localityName: String,
        ) = unlockedLocalitySource.isLocalityUnlocked(region, localityName)
    }
