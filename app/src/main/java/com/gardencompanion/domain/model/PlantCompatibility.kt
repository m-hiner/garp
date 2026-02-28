package com.gardencompanion.domain.model

data class PlantCompatibility(
    val plantId: String,
    val otherPlantId: String,
    val type: CompatibilityType,
)
