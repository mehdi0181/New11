package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.ContentMainScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StoryDetailScreen
import com.example.ui.screens.games.QuizGamesHubScreen
import com.example.ui.theme.AppTheme
import com.example.viewmodel.AppViewModel

enum class ScreenRoute {
    HOME,
    CONTENT_MAIN,
    STORY_DETAIL,
    SETTINGS,
    ABOUT,
    QUIZ_GAMES,
    AI_ASSISTANT
}

@Composable
fun AppNavigation(viewModel: AppViewModel) {
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()

    var currentScreen by remember { mutableStateOf(ScreenRoute.HOME) }
    var selectedStoryId by remember { mutableIntStateOf(-1) }

    AppTheme(themeMode = userSettings.themeMode) {

        when (currentScreen) {
            ScreenRoute.HOME -> {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToContent = { currentScreen = ScreenRoute.CONTENT_MAIN },
                    onNavigateToSettings = { currentScreen = ScreenRoute.SETTINGS },
                    onNavigateToAbout = { currentScreen = ScreenRoute.ABOUT },
                    onNavigateToQuizGames = { currentScreen = ScreenRoute.QUIZ_GAMES },
                    onNavigateToAiAssistant = { currentScreen = ScreenRoute.AI_ASSISTANT }
                )
            }

            ScreenRoute.CONTENT_MAIN -> {
                ContentMainScreen(
                    viewModel = viewModel,
                    onBackToHome = { currentScreen = ScreenRoute.HOME },
                    onSelectStory = { id ->
                        selectedStoryId = id
                        currentScreen = ScreenRoute.STORY_DETAIL
                    }
                )
            }

            ScreenRoute.STORY_DETAIL -> {
                StoryDetailScreen(
                    storyId = selectedStoryId,
                    viewModel = viewModel,
                    onBack = { currentScreen = ScreenRoute.CONTENT_MAIN }
                )
            }

            ScreenRoute.SETTINGS -> {
                SettingsScreen(
                    viewModel = viewModel,
                    onBack = { currentScreen = ScreenRoute.HOME }
                )
            }

            ScreenRoute.ABOUT -> {
                AboutScreen(
                    viewModel = viewModel,
                    onBack = { currentScreen = ScreenRoute.HOME }
                )
            }

            ScreenRoute.QUIZ_GAMES -> {
                QuizGamesHubScreen(
                    viewModel = viewModel,
                    onBack = { currentScreen = ScreenRoute.HOME }
                )
            }

            ScreenRoute.AI_ASSISTANT -> {
                AiAssistantScreen(
                    viewModel = viewModel,
                    onBack = { currentScreen = ScreenRoute.HOME }
                )
            }
        }
    }
}
