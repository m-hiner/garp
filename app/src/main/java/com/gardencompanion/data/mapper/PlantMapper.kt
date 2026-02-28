package com.gardencompanion.data.mapper

import com.gardencompanion.data.database.entity.PlantEntity
import com.gardencompanion.domain.model.Plant

object PlantMapper {
    fun toDomain(entity: PlantEntity): Plant {
        return Plant(
            id = entity.id,
            nameEn = entity.nameEn,
            nameSk = entity.nameSk,
            icon = entity.icon,
            family = entity.family,
        )
    }
}
