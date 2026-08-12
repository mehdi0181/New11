package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.StoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface StoryDao {
    @Query("SELECT * FROM stories ORDER BY id ASC")
    fun getAllStories(): Flow<List<StoryItem>>

    @Query("SELECT * FROM stories WHERE category = :category ORDER BY id ASC")
    fun getStoriesByCategory(category: String): Flow<List<StoryItem>>

    @Query("SELECT * FROM stories WHERE isFavorite = 1 ORDER BY id ASC")
    fun getFavoriteStories(): Flow<List<StoryItem>>

    @Query("SELECT * FROM stories WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY id ASC")
    fun searchStories(query: String): Flow<List<StoryItem>>

    @Query("SELECT * FROM stories WHERE id = :id")
    suspend fun getStoryById(id: Int): StoryItem?

    @Query("UPDATE stories SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Int, isFavorite: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stories: List<StoryItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: StoryItem): Long

    @Query("SELECT COUNT(*) FROM stories")
    suspend fun getCount(): Int

    @Query("SELECT title FROM stories")
    suspend fun getAllTitles(): List<String>
}
