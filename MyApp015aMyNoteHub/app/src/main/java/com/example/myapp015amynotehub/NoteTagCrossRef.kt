package com.example.myapp015amynotehub

import androidx.room.Entity

@Entity(tableName = "note_tag_cross_ref", primaryKeys = ["noteId", "tagId"])
data class NoteTagCrossRef(
    val noteId: Int,
    val tagId: Int
)
