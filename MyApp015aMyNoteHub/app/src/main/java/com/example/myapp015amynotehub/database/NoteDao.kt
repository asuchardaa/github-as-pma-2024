package com.example.myapp015amynotehub.database

import androidx.room.*
import com.example.myapp015amynotehub.models.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert
    suspend fun insert(note: Note): Long

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT * FROM note_table ORDER BY id DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM note_table WHERE categoryId = :categoryId")
    fun getNotesByCategory(categoryId: Int): List<Note>

    @Query(
        """
        SELECT * FROM note_table 
        INNER JOIN note_tag_cross_ref ON note_table.id = note_tag_cross_ref.noteId
        INNER JOIN tag_table ON note_tag_cross_ref.tagId = tag_table.id
        WHERE tag_table.name IN (:tags)
        GROUP BY note_table.id
        HAVING COUNT(DISTINCT tag_table.id) = :tagCount
    """
    )
    fun getNotesByTags(tags: List<String>, tagCount: Int = tags.size): List<Note>

    @Query(
        """
        SELECT * FROM note_table
        WHERE categoryId = :categoryId
        AND id IN (
            SELECT noteId FROM note_tag_cross_ref
            INNER JOIN tag_table ON note_tag_cross_ref.tagId = tag_table.id
            WHERE tag_table.name IN (:tags)
            GROUP BY noteId
            HAVING COUNT(DISTINCT tag_table.id) = :tagCount
        )
    """
    )
    fun getNotesByCategoryAndTags(categoryId: Int, tags: List<String>, tagCount: Int = tags.size): List<Note>
}
