package com.pmr2025.viallehosnieh.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending")
data class PendingEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    @ColumnInfo(name = "type")
    val type : String, // Type d'opération
    @ColumnInfo(name = "hash")
    val hash : String,
    @ColumnInfo(name = "list_id")
    val listId : Int ?= null,
    @ColumnInfo(name = "item_id")
    val itemId : Int ?= null,
    @ColumnInfo(name = "label")
    val label : String ?= null,
    @ColumnInfo(name = "checked")
    val checked : String ?= null,
    @ColumnInfo(name = "url")
    val url : String ?= null
)
