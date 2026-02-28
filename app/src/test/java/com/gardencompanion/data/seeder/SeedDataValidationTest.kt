package com.gardencompanion.data.seeder

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.File

class SeedDataValidationTest {

    private val json = Json { ignoreUnknownKeys = true }

    private fun readAsset(name: String): String =
        File("src/main/assets/$name").readText()

    private val plants: List<PlantJson> by lazy {
        json.decodeFromString<List<PlantJson>>(readAsset("plants.json"))
    }

    private val compatEntries: List<CompatibilityJson> by lazy {
        json.decodeFromString<List<CompatibilityJson>>(readAsset("compatibility.json"))
    }

    @Test
    fun compatibilityEntries_areSymmetric() {
        val byPair = compatEntries.groupBy { setOf(it.plantId, it.otherPlantId) }

        for ((pair, entries) in byPair) {
            val types = entries.map { it.type }.distinct()
            assertEquals(
                "Conflicting compatibility types for pair $pair: $types",
                1,
                types.size,
            )
        }
    }

    @Test
    fun compatibilityEntries_haveNoDuplicatePairs() {
        val seen = mutableSetOf<Set<String>>()

        for (entry in compatEntries) {
            val pair = setOf(entry.plantId, entry.otherPlantId)
            assertTrue(
                "Duplicate compatibility entry for pair $pair",
                seen.add(pair),
            )
        }
    }

    @Test
    fun compatibilityEntries_referenceValidPlantIds() {
        val validIds = plants.map { it.id }.toSet()

        for (entry in compatEntries) {
            assertTrue(
                "Unknown plantId '${entry.plantId}' in compatibility entry",
                entry.plantId in validIds,
            )
            assertTrue(
                "Unknown otherPlantId '${entry.otherPlantId}' in compatibility entry",
                entry.otherPlantId in validIds,
            )
        }
    }

    @Test
    fun compatibilityEntries_haveValidTypes() {
        val validTypes = setOf("BENEFICIAL", "NEUTRAL", "DETRIMENTAL")

        for (entry in compatEntries) {
            assertTrue(
                "Invalid compatibility type '${entry.type}' for pair (${entry.plantId}, ${entry.otherPlantId})",
                entry.type in validTypes,
            )
        }
    }

    @Test
    fun compatibilityEntries_doNotReferenceSelfPairs() {
        for (entry in compatEntries) {
            if (entry.plantId == entry.otherPlantId) {
                fail("Self-referencing compatibility entry for '${entry.plantId}'")
            }
        }
    }

    @Test
    fun plantEntries_haveUniqueIds() {
        val seen = mutableSetOf<String>()

        for (plant in plants) {
            assertTrue(
                "Duplicate plant ID '${plant.id}'",
                seen.add(plant.id),
            )
        }
    }
}
