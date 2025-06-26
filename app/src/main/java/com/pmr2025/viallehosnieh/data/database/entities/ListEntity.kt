package com.pmr2025.viallehosnieh.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "list")
data class ListEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,

    @ColumnInfo(name = "label")
    val label : String,

    @ColumnInfo(name = "user_hash")
    val userHash : String
)