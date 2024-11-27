package com.dorkytiger.top.db.dao.download

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface DownloadDao {

    @Insert
    suspend fun insert(download: DownloadEntity)

    @Query("SELECT * FROM downloads")
    suspend fun getAll(): List<DownloadEntity>

    @Update
    suspend fun update(download: DownloadEntity)

    @Delete
    suspend fun delete(download: DownloadEntity)
}