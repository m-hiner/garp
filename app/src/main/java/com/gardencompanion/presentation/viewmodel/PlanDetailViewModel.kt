package com.gardencompanion.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gardencompanion.domain.model.SubPlot
import com.gardencompanion.domain.repository.GardenRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlanDetailViewModel(
    private val planId: String,
    private val gardenRepository: GardenRepository,
) : ViewModel() {

    val subPlots: StateFlow<List<SubPlot>> = gardenRepository.observeSubPlots(planId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addSubPlot(name: String) {
        viewModelScope.launch {
            gardenRepository.addSubPlot(planId, name)
        }
    }

    fun deleteSubPlot(subPlotId: String) {
        viewModelScope.launch {
            gardenRepository.deleteSubPlot(subPlotId)
        }
    }

    fun renameSubPlot(subPlotId: String, newName: String) {
        viewModelScope.launch {
            gardenRepository.renameSubPlot(subPlotId, newName)
        }
    }
}
