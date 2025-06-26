package com.pmr2025.viallehosnieh.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pmr2025.viallehosnieh.data.database.dao.ItemDao
import com.pmr2025.viallehosnieh.data.database.dao.ListDao
import com.pmr2025.viallehosnieh.data.database.dao.PendingDao
import com.pmr2025.viallehosnieh.data.database.entities.ItemEntity
import com.pmr2025.viallehosnieh.data.database.entities.ListEntity
import com.pmr2025.viallehosnieh.data.database.entities.PendingEntity

@Database(entities = [ListEntity::class, ItemEntity::class, PendingEntity::class], version = 1)
abstract class AppDataBase : RoomDatabase(){
    abstract fun listDao() : ListDao
    abstract fun itemDao() : ItemDao
    abstract fun pendingDao() : PendingDao
}