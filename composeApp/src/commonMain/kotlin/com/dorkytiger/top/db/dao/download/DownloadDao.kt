package com.dorkytiger.top.db.dao.download

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DownloadDao {

    @Insert
    suspend fun insert(download: DownloadEntity)

    @Query("SELECT * FROM downloads")
    suspend fun getAll(): List<DownloadEntity>
}