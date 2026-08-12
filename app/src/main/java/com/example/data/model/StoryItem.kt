package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stories")
data class StoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val category: String, // "JOKE", "RIDDLE", "NASRUDDIN"
    val answer: String? = null,
    val isFavorite: Boolean = false,
    val readTimeMinutes: Int = 1
)
