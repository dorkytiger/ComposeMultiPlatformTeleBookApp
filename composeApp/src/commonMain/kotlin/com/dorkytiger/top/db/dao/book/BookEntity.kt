package com.dorkytiger.top.db.dao.book

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val pathUrls: List<String>
)