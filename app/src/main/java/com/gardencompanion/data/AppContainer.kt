package com.gardencompanion.data

import android.content.Context
import com.gardencompanion.data.database.GardenDatabase
import com.gardencompanion.data.repository.GardenRepositoryRoom
import com.gardencompanion.data.repository.PlantRepositoryRoom
import com.gardencompanion.data.repository.SettingsRepositoryDataStore
import com.gardencompanion.data.seeder.DatabaseSeeder
import com.gardencompanion.domain.repository.GardenRepository
import com.gardencompanion.domain.repository.PlantRepository
import com.gardencompanion.domain.service.CompanionPlantingService
import com.gardencompanion.domain.service.CropRotationService
import com.gardencompanion.domain.usecase.GetLocalizedPlantNameUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AppContainer(
    private val appContext: Context,
) {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val database: GardenDatabase = GardenDatabase.build(appContext)

    val settingsRepository = SettingsRepositoryDataStore(appContext)

    val plantRepository: PlantRepository = PlantRepositoryRoom(
        plantDao = database.plantDao(),
        compatibilityDao = database.plantCompatibilityDao(),
    )

    val gardenRepository: GardenRepository = GardenRepositoryRoom(
        gardenPlanDao = database.gardenPlanDao(),
        subPlotDao = database.subPlotDao(),
        rowDao = database.rowDao(),
    )

    val companionPlantingService = CompanionPlantingService(plantRepository)
    val cropRotationService = CropRotationService()

    val getLocalizedPlantNameUseCase = GetLocalizedPlantNameUseCase()

    init {
        applicationScope.launch {
            val seeded = settingsRepository.getDatabaseSeededOnce()
            if (!seeded) {
                DatabaseSeeder(appContext, database).seedIfEmpty()
                settingsRepository.setDatabaseSeededOnce(true)
            }
        }
    }
}
