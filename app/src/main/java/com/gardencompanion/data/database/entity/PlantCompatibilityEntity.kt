package com.gardencompanion.data.database.entity

import androidx.room.Entity
import com.gardencompanion.domain.model.CompatibilityType

@Entity(
    tableName = "plant_compatibility",
    primaryKeys = ["plantId", "otherPlantId"],
)
data class PlantCompatibilityEntity(
    val plantId: String,
    val otherPlantId: String,
    val type: CompatibilityType,
)
