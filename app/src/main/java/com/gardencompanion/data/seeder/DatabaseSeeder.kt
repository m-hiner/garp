package com.gardencompanion.data.seeder

import android.content.Context
import com.gardencompanion.data.database.GardenDatabase
import com.gardencompanion.data.database.entity.PlantCompatibilityEntity
import com.gardencompanion.data.database.entity.PlantEntity
import com.gardencompanion.domain.model.CompatibilityType
import com.gardencompanion.domain.model.PlantFamily
import kotlinx.serialization.json.Json

class DatabaseSeeder(
    private val context: Context,
    private val database: GardenDatabase,
) {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    suspend fun seedIfEmpty() {
        val plantCount = database.plantDao().count()
        val compatCount = database.plantCompatibilityDao().count()
        if (plantCount > 0 && compatCount > 0) return

        val plantsJson = readAsset("plants.json")
        val compatJson = readAsset("compatibility.json")

        val plants = json.decodeFromString<List<PlantJson>>(plantsJson).map { p ->
            PlantEntity(
                id = p.id,
                nameEn = p.nameEn,
                nameSk = p.nameSk,
                icon = p.icon,
                family = PlantFamily.entries.find { it.name == p.family } ?: PlantFamily.OTHER,
            )
        }

        val compat = json.decodeFromString<List<CompatibilityJson>>(compatJson).map { c ->
            PlantCompatibilityEntity(
                plantId = c.plantId,
                otherPlantId = c.otherPlantId,
                type = CompatibilityType.entries.find { it.name == c.type } ?: CompatibilityType.NEUTRAL,
            )
        }

        database.plantDao().insertAll(plants)
        database.plantCompatibilityDao().insertAll(compat)
    }

    private fun readAsset(name: String): String {
        return context.assets.open(name).bufferedReader().use { it.readText() }
    }
}
