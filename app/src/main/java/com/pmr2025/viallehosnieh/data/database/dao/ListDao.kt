package com.pmr2025.viallehosnieh.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pmr2025.viallehosnieh.data.database.entities.ListEntity

@Dao
interface ListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(list : ListEntity) : Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLists(lists : List<ListEntity>)

    @Query("SELECT * FROM list WHERE user_hash = :userHash")
    fun getLists(userHash:String): List<ListEntity>

    @Query("SELECT * FROM list WHERE id = :listId")
    fun getListInfos(listId:Int) : ListEntity?

    @Delete
    suspend fun deleteList(liste: ListEntity): Int
}