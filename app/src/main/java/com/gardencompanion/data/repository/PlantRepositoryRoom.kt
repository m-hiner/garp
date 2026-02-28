package com.gardencompanion.data.repository

import com.gardencompanion.data.database.dao.PlantCompatibilityDao
import com.gardencompanion.data.database.dao.PlantDao
import com.gardencompanion.data.mapper.PlantMapper
import com.gardencompanion.domain.model.CompatibilityType
import com.gardencompanion.domain.model.Plant
import com.gardencompanion.domain.repository.PlantRepository

class PlantRepositoryRoom(
    private val plantDao: PlantDao,
    private val compatibilityDao: PlantCompatibilityDao,
) : PlantRepository {
    override suspend fun getAllPlants(): List<Plant> {
        return plantDao.getAll().map(PlantMapper::toDomain)
    }

    override suspend fun getPlantById(id: String): Plant? {
        return plantDao.getById(id)?.let(PlantMapper::toDomain)
    }

    override suspend fun getCompatibilityTypeSymmetric(plantId: String, otherPlantId: String): CompatibilityType? {
        return compatibilityDao.getTypeSymmetric(plantId, otherPlantId)
    }
}
