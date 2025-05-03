package com.punyo.slatemap.application.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.punyo.slatemap.data.unlockedlocality.source.UnlockedLocalityDao
import com.punyo.slatemap.data.unlockedlocality.source.UnlockedLocalityEntity

@Database(entities = [UnlockedLocalityEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun unlockedLocalityDao(): UnlockedLocalityDao
}
