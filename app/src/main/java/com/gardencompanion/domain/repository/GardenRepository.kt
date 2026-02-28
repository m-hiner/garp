package com.gardencompanion.domain.repository

import com.gardencompanion.domain.model.GardenPlan
import com.gardencompanion.domain.model.Row
import com.gardencompanion.domain.model.SubPlot
import kotlinx.coroutines.flow.Flow

interface GardenRepository {
    suspend fun getOrCreatePlanForYear(year: Int): GardenPlan
    fun observePlans(): Flow<List<GardenPlan>>

    fun observeSubPlots(planId: String): Flow<List<SubPlot>>
    suspend fun addSubPlot(planId: String, name: String): SubPlot

    fun observeRows(subPlotId: String): Flow<List<Row>>
    suspend fun addRow(subPlotId: String): Row
    suspend fun reorderRows(subPlotId: String, orderedRowIds: List<String>)

    suspend fun assignPlant(rowId: String, plantId: String?)

    suspend fun getRowPlantIdForPreviousYearSamePosition(
        currentPlanYear: Int,
        subPlotId: String,
        orderIndex: Int,
    ): String?
}
