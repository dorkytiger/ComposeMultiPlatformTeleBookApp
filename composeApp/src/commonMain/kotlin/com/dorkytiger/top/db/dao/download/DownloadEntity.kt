package com.dorkytiger.top.db.dao.download

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val completeIndex: Int,
    val imgUrls: List<String>,
    val pathUrls: List<String>
)