package com.gardencompanion.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gardencompanion.data.database.entity.PlantCompatibilityEntity
import com.gardencompanion.domain.model.CompatibilityType

@Dao
interface PlantCompatibilityDao {
    @Query(
        "SELECT type FROM plant_compatibility " +
            "WHERE (plantId = :a AND otherPlantId = :b) OR (plantId = :b AND otherPlantId = :a) " +
            "LIMIT 1",
    )
    suspend fun getTypeSymmetric(a: String, b: String): CompatibilityType?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<PlantCompatibilityEntity>)

    @Query("SELECT COUNT(*) FROM plant_compatibility")
    suspend fun count(): Int
}
