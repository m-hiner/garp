package com.gardencompanion.domain.model

data class Plant(
    val id: String,
    val nameEn: String,
    val nameSk: String,
    val icon: String,
    val family: PlantFamily,
)
