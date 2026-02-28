package com.gardencompanion.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rows")
data class RowEntity(
    @PrimaryKey val id: String,
    val subPlotId: String,
    val orderIndex: Int,
    val plantId: String?,
)
