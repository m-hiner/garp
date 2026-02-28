package com.gardencompanion.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gardencompanion.domain.model.PlantFamily

@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey val id: String,
    val nameEn: String,
    val nameSk: String,
    val icon: String,
    val family: PlantFamily,
)
