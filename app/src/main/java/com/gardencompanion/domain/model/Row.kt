package com.gardencompanion.domain.model

data class Row(
    val id: String,
    val subPlotId: String,
    val orderIndex: Int,
    val plantId: String?,
)
