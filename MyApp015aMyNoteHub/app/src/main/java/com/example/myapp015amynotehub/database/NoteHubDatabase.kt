package com.example.myapp015amynotehub.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myapp015amynotehub.models.Category
import com.example.myapp015amynotehub.models.Note
import com.example.myapp015amynotehub.models.NoteTagCrossRef
import com.example.myapp015amynotehub.models.Tag

@Database(
    entities = [Note::class, Category::class, Tag::class, NoteTagCrossRef::class],
    version = 1,
    exportSchema = false
)
abstract class NoteHubDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun categoryDao(): CategoryDao
    abstract fun tagDao(): TagDao
    abstract fun noteTagDao(): NoteTagDao
}