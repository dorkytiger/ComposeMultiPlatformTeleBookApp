package com.dorkytiger.top.db

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

fun getAppDatabase(): RoomDatabase.Builder<AppDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "app.db")
//    if (dbFile.exists()) {
//        dbFile.delete()
//        println("Deleted existing database file: $dbFile")
//    }
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath,
    )
}