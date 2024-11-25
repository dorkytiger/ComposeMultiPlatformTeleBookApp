package com.dorkytiger.top.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookDao {

    @Insert
    suspend fun insert(book: BookEntity)

    @Query("SELECT * FROM books")
    suspend fun getAll(): List<BookEntity>

}