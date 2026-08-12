package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.StoryItem
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.preferences.UserSettings
import com.example.data.remote.GeminiApiClient
import com.example.data.repository.StoryRepository
import com.example.util.TextToSpeechHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChatMessage(
    val sender: String, // "USER" or "SHAHRAZAD"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = StoryRepository(db.storyDao())
    private val userPrefsRepository = UserPreferencesRepository(application)
    val ttsHelper = TextToSpeechHelper(application)

    val userSettings: StateFlow<UserSettings> = userPrefsRepository.settings

    private val _selectedCategory = MutableStateFlow("CATEGORIES_LIST") // "CATEGORIES_LIST", "EDUCATIONAL", "NASRUDDIN", "SHAHNAMEH", "JOKE", "RIDDLE", "FACT", "FAVORITE"
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _currentDetailStory = MutableStateFlow<StoryItem?>(null)
    val currentDetailStory: StateFlow<StoryItem?> = _currentDetailStory.asStateFlow()

    // --- AI States ---
    private val _aiInsight = MutableStateFlow<String?>(null)
    val aiInsight: StateFlow<String?> = _aiInsight.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _aiError = MutableStateFlow<String?>(null)
    val aiError: StateFlow<String?> = _aiError.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "SHAHRAZAD",
                text = "درود بر شما دوست گرامی! من شهرزاد، قصه گو و دستیار هوشمند شما هستم. بگو دوست داری درباره چه موضوعی داستان بنویسم یا چه چیستانی مطرح کنم؟ 😊✨"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _generatedStory = MutableStateFlow<StoryItem?>(null)
    val generatedStory: StateFlow<StoryItem?> = _generatedStory.asStateFlow()

    val isSpeaking: StateFlow<Boolean> = ttsHelper.isPlaying
    val speechRate: StateFlow<Float> = ttsHelper.speechRate

    init {
        viewModelScope.launch {
            repository.checkAndPrepopulate()
        }
    }

    val allStories: StateFlow<List<StoryItem>> = repository.allStories.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val storiesList: StateFlow<List<StoryItem>> =
        _searchQuery.flatMapLatest { query ->
            if (query.isNotBlank()) {
                repository.searchStories(query)
            } else {
                _selectedCategory.flatMapLatest { category ->
                    when (category) {
                        "FAVORITE" -> repository.favoriteStories
                        "EDUCATIONAL" -> repository.getStoriesByCategory("EDUCATIONAL")
                        "NASRUDDIN" -> repository.getStoriesByCategory("NASRUDDIN")
                        "SHAHNAMEH" -> repository.getStoriesByCategory("SHAHNAMEH")
                        "JOKE" -> repository.getStoriesByCategory("JOKE")
                        "RIDDLE" -> repository.getStoriesByCategory("RIDDLE")
                        "FACT" -> repository.getStoriesByCategory("FACT")
                        else -> repository.allStories
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        _searchQuery.value = ""
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun loadStoryDetail(id: Int) {
        viewModelScope.launch {
            _currentDetailStory.value = repository.getStoryById(id)
            _aiInsight.value = null // reset insight on story change
            _aiError.value = null
        }
    }

    fun toggleFavorite(story: StoryItem) {
        viewModelScope.launch {
            repository.toggleFavorite(story.id, story.isFavorite)
            if (_currentDetailStory.value?.id == story.id) {
                _currentDetailStory.value = _currentDetailStory.value?.copy(isFavorite = !story.isFavorite)
            }
        }
    }

    // --- AI Gemini Functions ---

    fun analyzeStoryMoral(title: String, content: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            _aiError.value = null
            val systemPrompt = "تو یک استاد ادبیات و روانشناس اخلاق هستی. داستان زیر را تحلیل کن و حکمت اصلی، درس اخلاقی و ارزش کاربردی آن را در زندگی امروزی در ۲ تا ۳ جمله کوتاه، دقیق و الهام‌بخش به زبان فارسی روان بیان کن."
            val userPrompt = "عنوان داستان: $title\nمتن داستان:\n$content"

            val result = GeminiApiClient.generateText(prompt = userPrompt, systemPrompt = systemPrompt)
            _isAiLoading.value = false
            result.onSuccess { text ->
                _aiInsight.value = text
            }.onFailure { error ->
                _aiError.value = error.localizedMessage
            }
        }
    }

    fun summarizeStory(title: String, content: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            _aiError.value = null
            val systemPrompt = "تو یک خلاصه‌نویس حرفه‌ای داستان هستی. داستان زیر را در حداکثر ۲ تا ۳ جمله بسیار جذاب، سریع و روان به زبان فارسی خلاصه کن."
            val userPrompt = "عنوان: $title\nمتن داستان:\n$content"

            val result = GeminiApiClient.generateText(prompt = userPrompt, systemPrompt = systemPrompt)
            _isAiLoading.value = false
            result.onSuccess { text ->
                _aiInsight.value = "📌 **خلاصه هوشمند داستان:**\n$text"
            }.onFailure { error ->
                _aiError.value = error.localizedMessage
            }
        }
    }

    fun createCustomAiStory(topic: String, categoryName: String, tone: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            _aiError.value = null
            _generatedStory.value = null

            val systemPrompt = "تو یک نویسنده برجسته داستان‌های شیرین فارسی هستی. یک داستان کوتاه جذاب، پندآموز و خلاقانه درباره موضوع درخواست‌شده بنویس. فرمت پاسخ باید دقیقاً شامل یک خط عنوان (مثلا: عنوان: ...) و سپس متن داستان باشد."
            val userPrompt = "موضوع داستان: $topic\nدسته‌بندی: $categoryName\nلحن و سبک: $tone"

            val result = GeminiApiClient.generateText(prompt = userPrompt, systemPrompt = systemPrompt)
            _isAiLoading.value = false

            result.onSuccess { fullText ->
                var title = "داستان هوش مصنوعی"
                var storyText = fullText

                if (fullText.contains("عنوان:")) {
                    val parts = fullText.split("عنوان:", limit = 2)
                    if (parts.size > 1) {
                        val subParts = parts[1].split("\n", limit = 2)
                        title = subParts[0].trim()
                        if (subParts.size > 1) {
                            storyText = subParts[1].trim()
                        }
                    }
                }

                val dbCategory = when (categoryName) {
                    "داستان‌های آموزنده" -> "EDUCATIONAL"
                    "داستان ملانصرالدین" -> "NASRUDDIN"
                    "داستان شاهنامه" -> "SHAHNAMEH"
                    "جک و لطیفه" -> "JOKE"
                    "چیستان" -> "RIDDLE"
                    else -> "FACT"
                }

                val newStory = StoryItem(
                    title = title.ifBlank { "داستان خلاقانه $topic" },
                    content = storyText,
                    category = dbCategory,
                    readTimeMinutes = 2
                )
                _generatedStory.value = newStory
            }.onFailure { error ->
                _aiError.value = error.localizedMessage
            }
        }
    }

    fun saveGeneratedStoryToDb(story: StoryItem, onSaved: () -> Unit) {
        viewModelScope.launch {
            repository.insertStory(story)
            onSaved()
        }
    }

    fun sendChatMessage(userText: String) {
        if (userText.isBlank()) return
        val currentList = _chatMessages.value.toMutableList()
        currentList.add(ChatMessage("USER", userText))
        _chatMessages.value = currentList

        viewModelScope.launch {
            _isAiLoading.value = true
            val systemPrompt = "تو شهرزاد، قصه گو و دستیار هوشمند اپلیکیشن «دنیای سرگرمی» هستی. لحنت بسیار صمیمی، محترمانه، پرانرژی و جذاب است. به سوالات ادبی، داستان‌ها، چیستان‌ها و گفتگوهای کاربر به زیبایی و کوتاهی (در ۲ تا ۴ جمله) پاسخ بده."
            val historyContext = currentList.takeLast(6).joinToString("\n") { "${it.sender}: ${it.text}" }

            val result = GeminiApiClient.generateText(prompt = historyContext, systemPrompt = systemPrompt)
            _isAiLoading.value = false

            val reply = result.getOrDefault("سپاس از پیام شما دوست عزیز! چه داستان دیگری مد نظرتان است؟ ✨")
            val updatedList = _chatMessages.value.toMutableList()
            updatedList.add(ChatMessage("SHAHRAZAD", reply))
            _chatMessages.value = updatedList
        }
    }

    // --- Text To Speech ---
    fun speakStory(text: String) {
        ttsHelper.speak(text)
    }

    fun stopSpeaking() {
        ttsHelper.stop()
    }

    fun setSpeechSpeed(speed: Float) {
        ttsHelper.setSpeed(speed)
    }

    override fun onCleared() {
        super.onCleared()
        ttsHelper.shutdown()
    }

    // --- Settings ---
    fun updateFontSize(size: Float) {
        userPrefsRepository.updateFontSize(size)
    }

    fun updateFontFamily(family: String) {
        userPrefsRepository.updateFontFamily(family)
    }

    fun updateFontColor(hex: String) {
        userPrefsRepository.updateFontColor(hex)
    }

    fun updateThemeMode(mode: String) {
        userPrefsRepository.updateThemeMode(mode)
        userPrefsRepository.updateFontColor("")
    }

    fun updateGlassMode(enabled: Boolean) {
        userPrefsRepository.updateGlassMode(enabled)
    }

    fun resetSettings() {
        userPrefsRepository.resetToDefaults()
    }

    fun addCoins(amount: Int) {}
    fun spendCoins(amount: Int): Boolean = true
    fun canClaimDailyReward(): Boolean = false
    fun getRemainingClaimTimeMillis(): Long = 0L
    fun claimDailyReward(): Boolean = true
}
