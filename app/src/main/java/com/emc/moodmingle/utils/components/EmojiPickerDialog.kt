package com.emc.moodmingle.utils.components

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.vanniktech.emoji.google.GoogleEmojiProvider
import kotlinx.coroutines.delay
import kotlin.collections.orEmpty

@SuppressLint("FrequentlyChangingValue")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmojiPickerDialog(columns: Int = 8, onEmojiSelected: (String) -> Unit) {
    // MEMOIZE CATEGORIES
    val categories = rememberGoogleEmojiCategories()
    val tabNames = remember { categories.keys.toList() }

    var selectedTabName by remember { mutableStateOf(tabNames.first()) }

    // EMOJI LOADING STATE
    val emojisForSelectedCategory = remember(selectedTabName) {
        categories[selectedTabName].orEmpty()
    }
    var visibleEmojiCount by remember { mutableIntStateOf(0) }
    val loadBatchSize = 50

    // SCROLL STATE FOR LAZY GRID
    val gridState = rememberLazyGridState()

    // INITIAL LOAD
    LaunchedEffect(selectedTabName) {
        visibleEmojiCount = loadBatchSize.coerceAtMost(emojisForSelectedCategory.size)
    }

    // LOAD MORE EMOJIS WHEN SCROLL NEARS THE END
    LaunchedEffect(gridState.firstVisibleItemIndex, visibleEmojiCount) {
        val totalItems = emojisForSelectedCategory.size
        if (gridState.firstVisibleItemIndex + gridState.layoutInfo.visibleItemsInfo.size >= visibleEmojiCount - 5
            && visibleEmojiCount < totalItems
        ) {
            // SMALL DELAY TO SIMULATE LOADING
            delay(50)
            visibleEmojiCount = (visibleEmojiCount + loadBatchSize).coerceAtMost(totalItems)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryDark)
            .padding(top = 48.dp, bottom = 42.dp)
    ) {
        // CATEGORY TABS
        LazyRow(
            modifier = Modifier.padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tabNames) { tabName ->
                val selected = tabName == selectedTabName

                Row(
                    modifier = Modifier
                        .clickable {
                            selectedTabName = tabName
                            visibleEmojiCount =
                                loadBatchSize.coerceAtMost(categories[tabName]?.size ?: 0)
                        }
                        .background(
                            brush = if (selected) BrushPrimaryGradient else SolidColor(Color.Transparent),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(getCategoryIcon(tabName)),
                        contentDescription = getCategoryName(tabName),
                        tint = if (selected) Color.White else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )

                    Text(
                        text = getCategoryName(tabName),
                        color = if (selected) Color.White else Color.Gray,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // EMOJI GRID
        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            val visibleEmojis = emojisForSelectedCategory.take(visibleEmojiCount)

            items(items = visibleEmojis, key = { it }) { emoji ->
                Text(
                    text = emoji,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .background(SecondaryDark, CircleShape)
                        .aspectRatio(1f)
                        .clickable { onEmojiSelected(emoji) }
                        .wrapContentSize(align = Alignment.Center)
                )
            }

            // LOADING INDICATOR AT THE BOTTOM
            if (visibleEmojiCount < emojisForSelectedCategory.size) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

fun getCategoryName(name: String): String = when (name) {
    "Faces" -> "Smiley & People"
    "Nature" -> "Animals & Nature"
    "Food" -> "Food & Drink"
    "Activities" -> "Activities"
    "Places" -> "Travel & Places"
    "Objects" -> "Objects"
    "Symbols" -> "Symbols"
    else -> "Flags"
}

fun getCategoryIcon(name: String): Int {
    return mappedCategories()[name] ?: 0
}

fun mappedCategories(): Map<String, Int> {
    val provider = GoogleEmojiProvider()

    return provider.categories.associate { category ->
        category.categoryNames["en"].orEmpty() to provider.getIcon(category)
    }
}