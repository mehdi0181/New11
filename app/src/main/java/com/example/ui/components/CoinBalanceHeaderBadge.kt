package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.viewmodel.AppViewModel

@Composable
fun CoinBalanceHeaderBadge(
    viewModel: AppViewModel? = null,
    coins: Int = 0,
    isDark: Boolean = true,
    modifier: Modifier = Modifier
) {
    // Score/Coin badge removed per user request
}
