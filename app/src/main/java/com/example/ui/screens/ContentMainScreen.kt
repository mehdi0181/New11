package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.StoryItem
import com.example.ui.components.CoinBalanceHeaderBadge
import com.example.ui.components.ScenicGlassContainer
import com.example.ui.components.WhiteBorderCard
import com.example.ui.theme.THEME_LIGHT_CYAN
import com.example.viewmodel.AppViewModel

data class CategoryInfo(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)

val CATEGORY_LIST = listOf(
    CategoryInfo("EDUCATIONAL", "داستان‌های آموزنده", "داستان‌های کوتاه پندآموز، آموزنده و هزار و یک شب", Icons.Default.MenuBook, Color(0xFF2563EB)),
    CategoryInfo("NASRUDDIN", "داستان ملانصرالدین", "داستان‌های طنزآمیز و پندآموز ملانصرالدین", Icons.Default.Psychology, Color(0xFF0284C7)),
    CategoryInfo("SHAHNAMEH", "داستان شاهنامه", "داستان‌های حماسی و ماندگار شاهنامه فردوسی", Icons.Default.AutoAwesome, Color(0xFF7C3AED)),
    CategoryInfo("JOKE", "جک و لطیفه", "لطیفه‌ها و شوخی‌های خنده‌دار و شاد", Icons.Default.SentimentVerySatisfied, Color(0xFF059669)),
    CategoryInfo("RIDDLE", "چیستان و معما", "چیستان‌های سرگرم‌کننده و معماهای هوش", Icons.Default.HelpOutline, Color(0xFFD97706)),
    CategoryInfo("FACT", "دانستنی‌ها", "حقایق جالب و شگفت‌انگیز علمی و عمومی", Icons.Default.Lightbulb, Color(0xFF0891B2)),
    CategoryInfo("FAVORITE", "علاقه‌مندی‌ها", "مطالب نشان‌شده و مورد علاقه شما", Icons.Default.Favorite, Color(0xFFE11D48))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentMainScreen(
    viewModel: AppViewModel,
    onBackToHome: () -> Unit,
    onSelectStory: (Int) -> Unit
) {
    val stories by viewModel.storiesList.collectAsStateWithLifecycle()
    val allStories by viewModel.allStories.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()

    val activeTheme = userSettings.themeMode
    val isDark = activeTheme != THEME_LIGHT_CYAN
    var isSearchOpen by remember { mutableStateOf(false) }

    ScenicGlassContainer(
        themeMode = activeTheme
    ) {

        Scaffold(
            topBar = {
                TopAppBar(
                title = {
                    if (isSearchOpen) {
                        // In-header Search Field
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.onSearchQueryChange(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("search_input_field"),
                            placeholder = {
                                Text(
                                    text = "جستجو در عنوان یا متن...",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(24.dp),
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "پاکسازی",
                                            tint = Color.White
                                        )
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White.copy(alpha = 0.2f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.15f),
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.app_icon_fg),
                                contentDescription = "لوگوی برنامه",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "فهرست جک و داستان‌ها",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (isSearchOpen) {
                        IconButton(
                            onClick = {
                                isSearchOpen = false
                                viewModel.onSearchQueryChange("")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "بستن جستجو",
                                tint = Color.White
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                if (selectedCategory != "CATEGORIES_LIST") {
                                    viewModel.selectCategory("CATEGORIES_LIST")
                                } else {
                                    onBackToHome()
                                }
                            },
                            modifier = Modifier.testTag("back_to_home_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "بازگشت"
                            )
                        }
                    }
                },
                actions = {
                    if (!isSearchOpen) {
                        CoinBalanceHeaderBadge(
                            viewModel = viewModel,
                            coins = userSettings.coins,
                            isDark = isDark
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        // Search Button Toggle
                        IconButton(
                            onClick = { isSearchOpen = true },
                            modifier = Modifier.testTag("toggle_search_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "جستجو",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (userSettings.isGlassMode) Color.White.copy(alpha = 0.22f) else MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        containerColor = if (userSettings.isGlassMode) Color.Transparent else MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // CONDITION 1: SEARCH IS ACTIVE (Search query is not blank)
            if (searchQuery.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "نتایج جستجو برای «$searchQuery»:",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                if (stories.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "هیچ موردی برای عبارت «$searchQuery» یافت نشد",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items = stories, key = { it.id }) { story ->
                            StoryTitleCard(
                                story = story,
                                isGlassMode = userSettings.isGlassMode,
                                onClick = { onSelectStory(story.id) },
                                onToggleFavorite = { viewModel.toggleFavorite(story) }
                            )
                        }
                    }
                }
            }
            // CONDITION 2: TOPICS / CATEGORIES LIST VIEW (When no category is selected yet)
            else if (selectedCategory == "CATEGORIES_LIST") {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Category,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "فهرست موضوعات",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 20.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "لطفاً برای مشاهده مطالب، موضوع مورد نظر خود را انتخاب کنید:",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }

                    items(CATEGORY_LIST) { category ->
                        val count = if (category.id == "FAVORITE") {
                            allStories.count { it.isFavorite }
                        } else {
                            allStories.count { it.category == category.id }
                        }
                        CategorySelectionCard(
                            category = category,
                            itemCount = count,
                            isGlassMode = userSettings.isGlassMode,
                            onClick = { viewModel.selectCategory(category.id) }
                        )
                    }
                }
            }
            // CONDITION 3: SPECIFIC TOPIC IS SELECTED (Displaying items for selected topic only)
            else {
                val currentCategoryInfo = CATEGORY_LIST.find { it.id == selectedCategory }
                    ?: CategoryInfo("CUSTOM", "مطالب", "لیست مطالب", Icons.Default.MenuBook, MaterialTheme.colorScheme.primary)

                Column(modifier = Modifier.fillMaxSize()) {
                    // Selected Category Header Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(currentCategoryInfo.color.copy(alpha = 0.12f))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(currentCategoryInfo.color),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = currentCategoryInfo.icon,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = currentCategoryInfo.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = currentCategoryInfo.color
                                    )
                                    Text(
                                        text = "${stories.size} مورد در این بخش",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                    )
                                }
                            }

                            // Button to return to Category List
                            Button(
                                onClick = { viewModel.selectCategory("CATEGORIES_LIST") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = currentCategoryInfo.color
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.List,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "تغییر موضوع",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    // Horizontal Quick Topic Chips Row
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { viewModel.selectCategory("CATEGORIES_LIST") },
                                label = { Text("📋 همه موضوعات", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        items(CATEGORY_LIST) { cat ->
                            val isSelected = selectedCategory == cat.id
                            val count = if (cat.id == "FAVORITE") {
                                allStories.count { it.isFavorite }
                            } else {
                                allStories.count { it.category == cat.id }
                            }
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectCategory(cat.id) },
                                label = {
                                    Text(
                                        text = "${cat.title} ($count)",
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = cat.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = cat.color,
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White,
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    labelColor = MaterialTheme.colorScheme.onSurface
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = MaterialTheme.colorScheme.outline,
                                    selectedBorderColor = cat.color,
                                    borderWidth = 1.5.dp
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    // Selected Topic Items List
                    if (stories.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = currentCategoryInfo.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = currentCategoryInfo.color.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = if (selectedCategory == "FAVORITE") "هنوز هیچ مطلبی به علاقه‌مندی‌ها اضافه نشده است." else "هیچ مطلبی در این موضوع یافت نشد.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(items = stories, key = { it.id }) { story ->
                                StoryTitleCard(
                                    story = story,
                                    isGlassMode = userSettings.isGlassMode,
                                    onClick = { onSelectStory(story.id) },
                                    onToggleFavorite = { viewModel.toggleFavorite(story) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
private fun CategorySelectionCard(
    category: CategoryInfo,
    itemCount: Int,
    isGlassMode: Boolean = false,
    onClick: () -> Unit
) {
    WhiteBorderCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("category_card_${category.id}"),
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.surface,
        borderColor = category.color.copy(alpha = 0.5f),
        borderWidth = 2.dp,
        isGlassMode = isGlassMode
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(category.color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = category.title,
                        tint = category.color,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = category.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        // Item Count Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(category.color.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "$itemCount",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = category.color
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = category.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                        )
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "مشاهده",
                tint = category.color,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun StoryTitleCard(
    story: StoryItem,
    isGlassMode: Boolean = false,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val categoryBadge = when (story.category) {
        "NASRUDDIN" -> "داستان ملانصرالدین" to Color(0xFF0284C7)
        "SHAHNAMEH" -> "داستان شاهنامه" to Color(0xFF7C3AED)
        "JOKE" -> "جک و لطیفه" to Color(0xFF059669)
        "RIDDLE" -> "چیستان" to Color(0xFFD97706)
        "FACT" -> "دانستنی‌ها" to Color(0xFF0891B2)
        else -> "متن" to Color(0xFF64748B)
    }

    WhiteBorderCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("story_card_${story.id}"),
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.surface,
        borderColor = MaterialTheme.colorScheme.outline,
        borderWidth = 2.dp,
        isGlassMode = isGlassMode
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(categoryBadge.second.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = categoryBadge.first,
                        color = categoryBadge.second,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Favorite Heart Button
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (story.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "علاقه‌مندی",
                        tint = if (story.isFavorite) Color(0xFFE11D48) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Story Title
            Text(
                text = story.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Content snippet
            Text(
                text = story.content,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
