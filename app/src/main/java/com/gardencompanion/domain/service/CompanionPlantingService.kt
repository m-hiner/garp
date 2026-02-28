package com.gardencompanion.domain.service

import com.gardencompanion.domain.model.CompanionCheckResult
import com.gardencompanion.domain.model.CompatibilityType
import com.gardencompanion.domain.model.NeighborCompatibility
import com.gardencompanion.domain.repository.PlantRepository

class CompanionPlantingService(
    private val plantRepository: PlantRepository,
) {
    suspend fun check(selectedPlantId: String, neighborPlantIds: List<String>): CompanionCheckResult {
        val results = neighborPlantIds.distinct().map { neighborId ->
            val type = plantRepository.getCompatibilityTypeSymmetric(selectedPlantId, neighborId)
                ?: CompatibilityType.NEUTRAL
            NeighborCompatibility(neighborPlantId = neighborId, type = type)
        }
        return CompanionCheckResult(selectedPlantId = selectedPlantId, neighborResults = results)
    }
}
