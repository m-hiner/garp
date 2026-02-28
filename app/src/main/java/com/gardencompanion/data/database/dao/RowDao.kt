package com.gardencompanion.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.gardencompanion.data.database.entity.RowEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RowDao {
    @Query("SELECT * FROM rows WHERE subPlotId = :subPlotId ORDER BY orderIndex")
    fun observeBySubPlotId(subPlotId: String): Flow<List<RowEntity>>

    @Query("SELECT MAX(orderIndex) FROM rows WHERE subPlotId = :subPlotId")
    suspend fun maxOrderIndex(subPlotId: String): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: RowEntity)

    @Query("UPDATE rows SET orderIndex = :orderIndex WHERE id = :rowId")
    suspend fun updateOrder(rowId: String, orderIndex: Int)

    @Query("UPDATE rows SET plantId = :plantId WHERE id = :rowId")
    suspend fun updatePlant(rowId: String, plantId: String?)

    @Query("SELECT * FROM rows WHERE subPlotId = :subPlotId AND orderIndex = :orderIndex LIMIT 1")
    suspend fun getBySubPlotAndOrder(subPlotId: String, orderIndex: Int): RowEntity?

    @Transaction
    suspend fun reorder(orderedRowIds: List<String>) {
        orderedRowIds.forEachIndexed { index, rowId ->
            updateOrder(rowId = rowId, orderIndex = index)
        }
    }
}
