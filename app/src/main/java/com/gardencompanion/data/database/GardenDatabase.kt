package com.gardencompanion.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gardencompanion.data.database.dao.GardenPlanDao
import com.gardencompanion.data.database.dao.PlantCompatibilityDao
import com.gardencompanion.data.database.dao.PlantDao
import com.gardencompanion.data.database.dao.RowDao
import com.gardencompanion.data.database.dao.SubPlotDao
import com.gardencompanion.data.database.entity.GardenPlanEntity
import com.gardencompanion.data.database.entity.PlantCompatibilityEntity
import com.gardencompanion.data.database.entity.PlantEntity
import com.gardencompanion.data.database.entity.RowEntity
import com.gardencompanion.data.database.entity.SubPlotEntity

@Database(
    entities = [
        PlantEntity::class,
        PlantCompatibilityEntity::class,
        GardenPlanEntity::class,
        SubPlotEntity::class,
        RowEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class GardenDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
    abstract fun plantCompatibilityDao(): PlantCompatibilityDao

    abstract fun gardenPlanDao(): GardenPlanDao
    abstract fun subPlotDao(): SubPlotDao
    abstract fun rowDao(): RowDao

    companion object {
        fun build(context: Context): GardenDatabase {
            return Room.databaseBuilder(
                context,
                GardenDatabase::class.java,
                "garden_companion.db",
            ).build()
        }
    }
}
