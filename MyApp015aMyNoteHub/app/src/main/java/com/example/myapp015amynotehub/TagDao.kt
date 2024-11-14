package com.example.myapp015amynotehub

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Insert
    suspend fun insert(tag: Tag)

    @Update
    suspend fun update(tag: Tag)

    @Delete
    suspend fun delete(tag: Tag)

    @Query("SELECT * FROM tag_table ORDER BY name ASC")
    fun getAllTags(): Flow<List<Tag>>
}
