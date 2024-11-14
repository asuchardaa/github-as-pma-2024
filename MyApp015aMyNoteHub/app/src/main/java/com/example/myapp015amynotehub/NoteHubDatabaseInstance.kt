package com.example.myapp015amynotehub

import android.content.Context
import androidx.room.Room

object NoteHubDatabaseInstance {
    @Volatile
    private var INSTANCE: NoteHubDatabase? = null

    fun getDatabase(context: Context): NoteHubDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                NoteHubDatabase::class.java,
                "notehub_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
