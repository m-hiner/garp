package com.gardencompanion.data.seeder

import kotlinx.serialization.Serializable

@Serializable
data class PlantJson(
    val id: String,
    val nameEn: String,
    val nameSk: String,
    val icon: String,
    val family: String,
)

@Serializable
data class CompatibilityJson(
    val plantId: String,
    val otherPlantId: String,
    val type: String,
)
