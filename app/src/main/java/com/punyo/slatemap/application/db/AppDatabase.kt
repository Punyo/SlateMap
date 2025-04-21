package com.punyo.slatemap.application.db

import androidx.room.RoomDatabase
import com.punyo.slatemap.data.unlockedlocality.source.UnlockedLocalityDao

abstract class AppDatabase : RoomDatabase() {
    abstract fun unlockedLocalityDao(): UnlockedLocalityDao
}
