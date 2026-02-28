package com.gardencompanion.domain.service

import com.gardencompanion.domain.model.CompatibilityType
import com.gardencompanion.domain.model.Plant
import com.gardencompanion.domain.model.PlantFamily
import com.gardencompanion.domain.repository.PlantRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class CompanionPlantingServiceTest {

    private class FakePlantRepository(
        private val compat: Map<Set<String>, CompatibilityType>,
    ) : PlantRepository {

        override suspend fun getAllPlants(): List<Plant> = emptyList()

        override suspend fun getPlantById(id: String): Plant? {
            return Plant(id = id, nameEn = id, nameSk = id, icon = id, family = PlantFamily.OTHER)
        }

        override suspend fun getCompatibilityTypeSymmetric(plantId: String, otherPlantId: String): CompatibilityType? {
            return compat[setOf(plantId, otherPlantId)]
        }
    }

    @Test
    fun beneficialCompatibility_isReturned() = runBlocking {
        val repo = FakePlantRepository(
            compat = mapOf(setOf("tomato", "basil") to CompatibilityType.BENEFICIAL),
        )
        val service = CompanionPlantingService(repo)

        val result = service.check(selectedPlantId = "tomato", neighborPlantIds = listOf("basil"))

        assertEquals(1, result.neighborResults.size)
        assertEquals(CompatibilityType.BENEFICIAL, result.neighborResults.first().type)
    }

    @Test
    fun detrimentalCompatibility_isReturned() = runBlocking {
        val repo = FakePlantRepository(
            compat = mapOf(setOf("tomato", "cabbage") to CompatibilityType.DETRIMENTAL),
        )
        val service = CompanionPlantingService(repo)

        val result = service.check(selectedPlantId = "tomato", neighborPlantIds = listOf("cabbage"))

        assertEquals(CompatibilityType.DETRIMENTAL, result.neighborResults.first().type)
    }

    @Test
    fun neutralFallback_isReturnedWhenNoMappingExists() = runBlocking {
        val repo = FakePlantRepository(compat = emptyMap())
        val service = CompanionPlantingService(repo)

        val result = service.check(selectedPlantId = "tomato", neighborPlantIds = listOf("unknown"))

        assertEquals(CompatibilityType.NEUTRAL, result.neighborResults.first().type)
    }
}
