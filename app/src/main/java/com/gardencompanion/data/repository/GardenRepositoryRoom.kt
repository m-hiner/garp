package com.gardencompanion.data.repository

import com.gardencompanion.data.database.dao.GardenPlanDao
import com.gardencompanion.data.database.dao.RowDao
import com.gardencompanion.data.database.dao.SubPlotDao
import com.gardencompanion.data.database.entity.GardenPlanEntity
import com.gardencompanion.data.database.entity.RowEntity
import com.gardencompanion.data.database.entity.SubPlotEntity
import com.gardencompanion.data.mapper.GardenMapper
import com.gardencompanion.domain.model.GardenPlan
import com.gardencompanion.domain.model.Row
import com.gardencompanion.domain.model.SubPlot
import com.gardencompanion.domain.repository.GardenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class GardenRepositoryRoom(
    private val gardenPlanDao: GardenPlanDao,
    private val subPlotDao: SubPlotDao,
    private val rowDao: RowDao,
) : GardenRepository {

    override suspend fun getOrCreatePlanForYear(year: Int): GardenPlan {
        val existing = gardenPlanDao.getByYear(year)
        if (existing != null) return GardenMapper.toDomain(existing)

        val entity = GardenPlanEntity(id = UUID.randomUUID().toString(), year = year)
        gardenPlanDao.insert(entity)
        return GardenMapper.toDomain(entity)
    }

    override fun observePlans(): Flow<List<GardenPlan>> {
        return gardenPlanDao.observeAll().map { list -> list.map(GardenMapper::toDomain) }
    }

    override fun observeSubPlots(planId: String): Flow<List<SubPlot>> {
        return subPlotDao.observeByPlanId(planId).map { list -> list.map(GardenMapper::toDomain) }
    }

    override suspend fun addSubPlot(planId: String, name: String): SubPlot {
        val nextIndex = (subPlotDao.maxOrderIndex(planId) ?: -1) + 1
        val entity = SubPlotEntity(
            id = UUID.randomUUID().toString(),
            gardenPlanId = planId,
            name = name,
            orderIndex = nextIndex,
        )
        subPlotDao.insert(entity)
        return GardenMapper.toDomain(entity)
    }

    override fun observeRows(subPlotId: String): Flow<List<Row>> {
        return rowDao.observeBySubPlotId(subPlotId).map { list -> list.map(GardenMapper::toDomain) }
    }

    override suspend fun addRow(subPlotId: String): Row {
        val nextIndex = (rowDao.maxOrderIndex(subPlotId) ?: -1) + 1
        val entity = RowEntity(
            id = UUID.randomUUID().toString(),
            subPlotId = subPlotId,
            orderIndex = nextIndex,
            plantId = null,
        )
        rowDao.insert(entity)
        return GardenMapper.toDomain(entity)
    }

    override suspend fun reorderRows(subPlotId: String, orderedRowIds: List<String>) {
        rowDao.reorder(orderedRowIds)
    }

    override suspend fun deleteSubPlot(subPlotId: String) {
        subPlotDao.deleteById(subPlotId)
    }

    override suspend fun renameSubPlot(subPlotId: String, newName: String) {
        subPlotDao.updateName(subPlotId, newName)
    }

    override suspend fun deleteRow(rowId: String) {
        rowDao.deleteById(rowId)
    }

    override suspend fun assignPlant(rowId: String, plantId: String?) {
        rowDao.updatePlant(rowId, plantId)
    }

    override suspend fun getRowPlantIdForPreviousYearSamePosition(
        currentPlanYear: Int,
        subPlotId: String,
        orderIndex: Int,
    ): String? {
        val currentSubPlot = subPlotDao.getById(subPlotId) ?: return null
        val previousPlan = gardenPlanDao.getByYear(currentPlanYear - 1) ?: return null

        val previousSubPlot = subPlotDao.getByPlanAndName(previousPlan.id, currentSubPlot.name)
            ?: subPlotDao.getByPlanAndOrderIndex(previousPlan.id, currentSubPlot.orderIndex)
            ?: return null

        val previousRow = rowDao.getBySubPlotAndOrder(previousSubPlot.id, orderIndex) ?: return null
        return previousRow.plantId
    }
}
