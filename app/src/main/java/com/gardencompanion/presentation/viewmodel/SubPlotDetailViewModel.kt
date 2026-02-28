package com.gardencompanion.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gardencompanion.data.repository.SettingsRepositoryDataStore
import com.gardencompanion.domain.model.CompatibilityType
import com.gardencompanion.domain.model.Row
import com.gardencompanion.domain.repository.GardenRepository
import com.gardencompanion.domain.repository.PlantRepository
import com.gardencompanion.domain.usecase.GetLocalizedPlantNameUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SubPlotDetailViewModel(
    private val subPlotId: String,
    private val plantRepository: PlantRepository,
    private val gardenRepository: GardenRepository,
    private val settingsRepository: SettingsRepositoryDataStore,
) : ViewModel() {

    private val getLocalizedPlantNameUseCase = GetLocalizedPlantNameUseCase()

    val rows: StateFlow<List<Row>> = gardenRepository.observeRows(subPlotId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addRow() {
        viewModelScope.launch {
            gardenRepository.addRow(subPlotId)
        }
    }

    fun clearRow(rowId: String) {
        viewModelScope.launch {
            gardenRepository.assignPlant(rowId, null)
        }
    }

    fun reorder(orderedRowIds: List<String>) {
        viewModelScope.launch {
            gardenRepository.reorderRows(subPlotId, orderedRowIds)
        }
    }

    suspend fun getCompatibilityType(plantId: String, otherPlantId: String): CompatibilityType {
        return plantRepository.getCompatibilityTypeSymmetric(plantId, otherPlantId)
            ?: CompatibilityType.NEUTRAL
    }

    suspend fun getPlantDisplayName(plantId: String, languageTag: String?): String? {
        val plant = plantRepository.getPlantById(plantId) ?: return null
        return getLocalizedPlantNameUseCase.execute(plant, languageTag)
    }
}
