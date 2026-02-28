package com.gardencompanion.data.mapper

import com.gardencompanion.data.database.entity.GardenPlanEntity
import com.gardencompanion.data.database.entity.RowEntity
import com.gardencompanion.data.database.entity.SubPlotEntity
import com.gardencompanion.domain.model.GardenPlan
import com.gardencompanion.domain.model.Row
import com.gardencompanion.domain.model.SubPlot

object GardenMapper {
    fun toDomain(entity: GardenPlanEntity): GardenPlan = GardenPlan(id = entity.id, year = entity.year)

    fun toDomain(entity: SubPlotEntity): SubPlot {
        return SubPlot(
            id = entity.id,
            gardenPlanId = entity.gardenPlanId,
            name = entity.name,
            orderIndex = entity.orderIndex,
        )
    }

    fun toDomain(entity: RowEntity): Row {
        return Row(
            id = entity.id,
            subPlotId = entity.subPlotId,
            orderIndex = entity.orderIndex,
            plantId = entity.plantId,
        )
    }
}
