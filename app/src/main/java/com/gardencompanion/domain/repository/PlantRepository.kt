package com.gardencompanion.domain.repository

import com.gardencompanion.domain.model.CompatibilityType
import com.gardencompanion.domain.model.Plant

interface PlantRepository {
    suspend fun getAllPlants(): List<Plant>
    suspend fun getPlantById(id: String): Plant?

    suspend fun getCompatibilityTypeSymmetric(plantId: String, otherPlantId: String): CompatibilityType?
}
