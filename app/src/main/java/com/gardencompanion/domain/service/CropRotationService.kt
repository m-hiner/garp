package com.gardencompanion.domain.service

import com.gardencompanion.domain.model.CropRotationResult
import com.gardencompanion.domain.model.PlantFamily

class CropRotationService {
    fun check(
        enabled: Boolean,
        selectedFamily: PlantFamily,
        previousFamilyAtSamePosition: PlantFamily?,
    ): CropRotationResult {
        if (!enabled) return CropRotationResult(isWarning = false)
        if (previousFamilyAtSamePosition == null) return CropRotationResult(isWarning = false)
        return CropRotationResult(isWarning = previousFamilyAtSamePosition == selectedFamily)
    }
}
