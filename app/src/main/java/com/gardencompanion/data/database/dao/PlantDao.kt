package com.gardencompanion.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gardencompanion.data.database.entity.PlantEntity

@Dao
interface PlantDao {
    @Query("SELECT * FROM plants ORDER BY nameEn")
    suspend fun getAll(): List<PlantEntity>

    @Query("SELECT * FROM plants WHERE id = :id")
    suspend fun getById(id: String): PlantEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<PlantEntity>)

    @Query("SELECT COUNT(*) FROM plants")
    suspend fun count(): Int
}
