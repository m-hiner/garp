package com.gardencompanion.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gardencompanion.domain.model.GardenPlan
import com.gardencompanion.domain.repository.GardenRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class GardenPlansViewModel(
    private val gardenRepository: GardenRepository,
) : ViewModel() {

    val plans: StateFlow<List<GardenPlan>> = gardenRepository.observePlans()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun createCurrentYearPlan(onCreated: (GardenPlan) -> Unit) {
        val year = LocalDate.now().year
        viewModelScope.launch {
            val plan = gardenRepository.getOrCreatePlanForYear(year)
            onCreated(plan)
        }
    }
}
