package com.gardencompanion.domain.model

data class CompanionCheckResult(
    val selectedPlantId: String,
    val neighborResults: List<NeighborCompatibility>,
)
