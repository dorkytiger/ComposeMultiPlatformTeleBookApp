package com.dorkytiger.top.db.dao.download

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val completeIndex: Int = 0,
    val imgUrls: List<String> = emptyList(),
    val pathUrls: List<String> = emptyList()
)