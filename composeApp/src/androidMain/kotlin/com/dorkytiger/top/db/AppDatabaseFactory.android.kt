package com.dorkytiger.top.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getAppDatabase(context: Context): RoomDatabase.Builder<AppDatabase> {
    val dbFile = context.getDatabasePath("app.db")
    return Room.databaseBuilder(
        context = context,
        name = dbFile.absolutePath
    )
}