package com.gardencompanion.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "garden_plans")
data class GardenPlanEntity(
    @PrimaryKey val id: String,
    val year: Int,
)
