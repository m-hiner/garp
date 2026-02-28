package com.gardencompanion.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gardencompanion.data.repository.SettingsRepositoryDataStore
import com.gardencompanion.domain.model.CompanionCheckResult
import com.gardencompanion.domain.model.CompatibilityType
import com.gardencompanion.domain.model.CropRotationResult
import com.gardencompanion.domain.model.Plant
import com.gardencompanion.domain.model.PlantFamily
import com.gardencompanion.domain.repository.GardenRepository
import com.gardencompanion.domain.repository.PlantRepository
import com.gardencompanion.domain.service.CompanionPlantingService
import com.gardencompanion.domain.service.CropRotationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class PlantSelectionUiState(
    val query: String = "",
    val plants: List<Plant> = emptyList(),
    val filteredPlants: List<Plant> = emptyList(),
    val pendingDecision: PendingDecision? = null,
)

data class PendingDecision(
    val selectedPlantId: String,
    val companionCheck: CompanionCheckResult,
    val cropRotation: CropRotationResult,
    val hasDetrimentalNeighbor: Boolean,
)

class PlantSelectionViewModel(
    private val planYear: Int,
    private val subPlotId: String,
    private val rowId: String,
    private val plantRepository: PlantRepository,
    private val gardenRepository: GardenRepository,
    private val settingsRepository: SettingsRepositoryDataStore,
    private val companionPlantingService: CompanionPlantingService,
    private val cropRotationService: CropRotationService,
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val plants = MutableStateFlow<List<Plant>>(emptyList())

    private val _uiState = MutableStateFlow(PlantSelectionUiState())
    val uiState: StateFlow<PlantSelectionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val all = plantRepository.getAllPlants()
            plants.value = all
        }

        viewModelScope.launch {
            combine(query, plants) { q, all ->
                val filtered = if (q.isBlank()) all else all.filter {
                    it.nameEn.contains(q, ignoreCase = true) || it.nameSk.contains(q, ignoreCase = true)
                }
                PlantSelectionUiState(query = q, plants = all, filteredPlants = filtered, pendingDecision = _uiState.value.pendingDecision)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun updateQuery(value: String) {
        query.value = value
    }

    fun selectPlant(plantId: String, onDone: () -> Unit) {
        viewModelScope.launch {
            val selected = plantRepository.getPlantById(plantId) ?: return@launch

            val rows = gardenRepository.observeRows(subPlotId).first()
            val targetRow = rows.firstOrNull { it.id == rowId } ?: return@launch

            val neighbors = rows
                .filter { it.orderIndex == targetRow.orderIndex - 1 || it.orderIndex == targetRow.orderIndex + 1 }
                .mapNotNull { it.plantId }

            val companion = companionPlantingService.check(selectedPlantId = plantId, neighborPlantIds = neighbors)
            val hasDetrimental = companion.neighborResults.any { it.type == CompatibilityType.DETRIMENTAL }

            val rotationEnabled = settingsRepository.cropRotationEnabled.first()
            val previousPlantId = gardenRepository.getRowPlantIdForPreviousYearSamePosition(
                currentPlanYear = planYear,
                subPlotId = subPlotId,
                orderIndex = targetRow.orderIndex,
            )
            val previousFamily = previousPlantId?.let { id -> plantRepository.getPlantById(id)?.family }
            val rotation = cropRotationService.check(
                enabled = rotationEnabled,
                selectedFamily = selected.family,
                previousFamilyAtSamePosition = previousFamily,
            )

            val needsConfirm = hasDetrimental || rotation.isWarning
            if (needsConfirm) {
                _uiState.value = _uiState.value.copy(
                    pendingDecision = PendingDecision(
                        selectedPlantId = plantId,
                        companionCheck = companion,
                        cropRotation = rotation,
                        hasDetrimentalNeighbor = hasDetrimental,
                    ),
                )
            } else {
                gardenRepository.assignPlant(rowId, plantId)
                _uiState.value = _uiState.value.copy(pendingDecision = null)
                onDone()
            }
        }
    }

    fun confirmKeepAnyway(onDone: () -> Unit) {
        val pending = _uiState.value.pendingDecision ?: return
        viewModelScope.launch {
            gardenRepository.assignPlant(rowId, pending.selectedPlantId)
            _uiState.value = _uiState.value.copy(pendingDecision = null)
            onDone()
        }
    }

    fun dismissDecision() {
        _uiState.value = _uiState.value.copy(pendingDecision = null)
    }
}
