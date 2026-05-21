package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Job::class, UserApplication::class, UserProfile::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract val jobDao: JobDao
}
