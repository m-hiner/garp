package com.gardencompanion.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.gardencompanion.data.database.entity.SubPlotEntity

@Dao
interface SubPlotDao {
    @Query("SELECT * FROM subplots WHERE gardenPlanId = :planId ORDER BY orderIndex")
    fun observeByPlanId(planId: String): Flow<List<SubPlotEntity>>

    @Query("SELECT * FROM subplots WHERE gardenPlanId = :planId AND name = :name LIMIT 1")
    suspend fun getByPlanAndName(planId: String, name: String): SubPlotEntity?

    @Query("SELECT * FROM subplots WHERE gardenPlanId = :planId AND orderIndex = :orderIndex LIMIT 1")
    suspend fun getByPlanAndOrderIndex(planId: String, orderIndex: Int): SubPlotEntity?

    @Query("SELECT MAX(orderIndex) FROM subplots WHERE gardenPlanId = :planId")
    suspend fun maxOrderIndex(planId: String): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SubPlotEntity)

    @Query("SELECT * FROM subplots WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): SubPlotEntity?
}
