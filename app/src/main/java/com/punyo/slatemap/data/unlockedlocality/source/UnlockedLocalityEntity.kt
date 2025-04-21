package com.punyo.slatemap.data.unlockedlocality.source

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "unlocked_locality")
data class UnlockedLocalityEntity(
    @PrimaryKey val localityName: String,
    val unlockedDate: String,
    val region: String,
)
