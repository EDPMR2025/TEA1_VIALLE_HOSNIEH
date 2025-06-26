package com.pmr2025.viallehosnieh.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pmr2025.viallehosnieh.data.database.entities.ItemEntity

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItemInList(item : ItemEntity) : Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItemsInList(items : List<ItemEntity>)

    @Query("SELECT * FROM item WHERE listId = :listId")
    fun getItemsFromList(listId: Int): List<ItemEntity>

    @Query("UPDATE item SET label = :label WHERE id = :itemId")
    suspend fun updateItemName(itemId : Int, label : String) : Int

    @Query("UPDATE item SET checked = :checked WHERE id = :itemId")
    suspend fun updateItemChecked(itemId : Int, checked : String) : Int

    @Delete
    suspend fun deleteItem(item: ItemEntity) : Int
}