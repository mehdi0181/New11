package com.example.data.repository

import com.example.data.local.StoryDao
import com.example.data.model.StoryItem
import kotlinx.coroutines.flow.Flow

class StoryRepository(private val storyDao: StoryDao) {

    val allStories: Flow<List<StoryItem>> = storyDao.getAllStories()
    val favoriteStories: Flow<List<StoryItem>> = storyDao.getFavoriteStories()

    suspend fun checkAndPrepopulate() {
        if (storyDao.getCount() == 0) {
            storyDao.insertAll(InitialData.sampleStories)
        } else {
            val existingTitles = storyDao.getAllTitles().toSet()
            val newStories = InitialData.sampleStories.filter { it.title !in existingTitles }
            if (newStories.isNotEmpty()) {
                storyDao.insertAll(newStories)
            }
        }
    }

    fun getStoriesByCategory(category: String): Flow<List<StoryItem>> {
        return storyDao.getStoriesByCategory(category)
    }

    fun searchStories(query: String): Flow<List<StoryItem>> {
        return storyDao.searchStories(query)
    }

    suspend fun getStoryById(id: Int): StoryItem? {
        return storyDao.getStoryById(id)
    }

    suspend fun toggleFavorite(id: Int, currentIsFavorite: Boolean) {
        storyDao.updateFavorite(id, !currentIsFavorite)
    }

    suspend fun insertStory(story: StoryItem): Long {
        return storyDao.insertStory(story)
    }
}
