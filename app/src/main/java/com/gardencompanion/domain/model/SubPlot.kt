package com.gardencompanion.domain.model

data class SubPlot(
    val id: String,
    val gardenPlanId: String,
    val name: String,
    val orderIndex: Int,
)
