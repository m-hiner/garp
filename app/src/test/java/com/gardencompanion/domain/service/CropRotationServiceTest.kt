package com.gardencompanion.domain.service

import com.gardencompanion.domain.model.PlantFamily
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CropRotationServiceTest {

    @Test
    fun cropRotationFamilyValidation_warnsWhenSameFamily() {
        val service = CropRotationService()

        val result = service.check(
            enabled = true,
            selectedFamily = PlantFamily.SOLANACEAE,
            previousFamilyAtSamePosition = PlantFamily.SOLANACEAE,
        )

        assertTrue(result.isWarning)
    }

    @Test
    fun cropRotationFamilyValidation_noWarningWhenDifferentFamily() {
        val service = CropRotationService()

        val result = service.check(
            enabled = true,
            selectedFamily = PlantFamily.SOLANACEAE,
            previousFamilyAtSamePosition = PlantFamily.APIACEAE,
        )

        assertFalse(result.isWarning)
    }

    @Test
    fun cropRotationFamilyValidation_noWarningWhenDisabled() {
        val service = CropRotationService()

        val result = service.check(
            enabled = false,
            selectedFamily = PlantFamily.SOLANACEAE,
            previousFamilyAtSamePosition = PlantFamily.SOLANACEAE,
        )

        assertFalse(result.isWarning)
    }
}
