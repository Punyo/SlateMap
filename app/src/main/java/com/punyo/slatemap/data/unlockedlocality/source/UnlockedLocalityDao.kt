package com.punyo.slatemap.data.unlockedlocality.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UnlockedLocalityDao {
    @Query("SELECT * FROM unlocked_locality WHERE region = :regions")
    suspend fun getUnlockedLocalityByRegion(regions: String): List<UnlockedLocalityEntity>

    @Query("DELETE FROM unlocked_locality")
    suspend fun deleteAllUnlockedLocality()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnlockedLocality(unlockedLocality: UnlockedLocalityEntity)
}
