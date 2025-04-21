package com.punyo.slatemap.data.unlockedlocality.source

import com.punyo.slatemap.application.Regions

class UnlockedLocalitySource(
    private val unlockedLocalityDao: UnlockedLocalityDao,
) {
    suspend fun deleteAllUnlockedLocalities() = unlockedLocalityDao.deleteAllUnlockedLocality()

    suspend fun getUnlockedLocalityByRegion(region: Regions): List<UnlockedLocalityEntity> =
        unlockedLocalityDao.getUnlockedLocalityByRegion(region.toString())

    suspend fun insertUnlockedLocality(unlockedLocality: UnlockedLocalityEntity) =
        unlockedLocalityDao.insertUnlockedLocality(unlockedLocality)

    suspend fun isLocalityUnlocked(
        region: Regions,
        localityName: String,
    ): Boolean =
        unlockedLocalityDao.getUnlockedLocalityByRegion(region.toString()).any {
            it.localityName == localityName
        }
}
