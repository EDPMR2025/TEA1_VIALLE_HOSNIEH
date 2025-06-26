package com.pmr2025.viallehosnieh.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    @ColumnInfo(name = "listId")
    val listId: Int, // clé étrangère vers la liste
    @ColumnInfo(name = "label")
    val label : String,
    @ColumnInfo(name = "checked")
    val checked : String,
    @ColumnInfo(name = "url")
    val url : String ?= ""
)

