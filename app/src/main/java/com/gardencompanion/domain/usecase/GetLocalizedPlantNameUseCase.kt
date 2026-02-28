package com.gardencompanion.domain.usecase

import com.gardencompanion.domain.model.Plant

class GetLocalizedPlantNameUseCase {
    fun execute(plant: Plant, languageTag: String?): String {
        val lang = languageTag?.lowercase()?.substringBefore('-')
        return if (lang == "sk") plant.nameSk else plant.nameEn
    }
}
