package com.gardencompanion.data.database

import androidx.room.TypeConverter
import com.gardencompanion.domain.model.CompatibilityType
import com.gardencompanion.domain.model.PlantFamily

class Converters {
    @TypeConverter
    fun plantFamilyFromString(value: String): PlantFamily =
        PlantFamily.entries.find { it.name == value } ?: PlantFamily.OTHER

    @TypeConverter
    fun plantFamilyToString(value: PlantFamily): String = value.name

    @TypeConverter
    fun compatibilityTypeFromString(value: String): CompatibilityType =
        CompatibilityType.entries.find { it.name == value } ?: CompatibilityType.NEUTRAL

    @TypeConverter
    fun compatibilityTypeToString(value: CompatibilityType): String = value.name
}
