package com.pmr2025.viallehosnieh.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pmr2025.viallehosnieh.data.database.entities.PendingEntity

@Dao
interface PendingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPending(pending: PendingEntity)

    @Query("SELECT * FROM pending")
    fun getAllPending() : List<PendingEntity>

    @Query("DELETE FROM pending")
    suspend fun deleteAllPending() : Int
}