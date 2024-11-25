package com.dorkytiger.top.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.dorkytiger.top.db.dao.BookDao
import com.dorkytiger.top.db.dao.BookEntity
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json


@Database(entities = [BookEntity::class], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
@TypeConverters(AppTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun bookDao(): BookDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

class AppTypeConverter {
    @TypeConverter
    fun fromString(value: String): List<String> {
        return Json.decodeFromString(
            ListSerializer(String.serializer()), value
        )
    }

    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return Json.encodeToString(
            ListSerializer(String.serializer()), list
        )
    }

}
