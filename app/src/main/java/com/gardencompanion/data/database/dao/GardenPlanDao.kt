package com.gardencompanion.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.gardencompanion.data.database.entity.GardenPlanEntity

@Dao
interface GardenPlanDao {
    @Query("SELECT * FROM garden_plans ORDER BY year DESC")
    fun observeAll(): Flow<List<GardenPlanEntity>>

    @Query("SELECT * FROM garden_plans WHERE year = :year LIMIT 1")
    suspend fun getByYear(year: Int): GardenPlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: GardenPlanEntity)
}
