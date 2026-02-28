package com.gardencompanion.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subplots")
data class SubPlotEntity(
    @PrimaryKey val id: String,
    val gardenPlanId: String,
    val name: String,
    val orderIndex: Int,
)
