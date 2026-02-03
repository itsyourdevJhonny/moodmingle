package com.emc.moodmingle.utils.emojipicker

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.emc.moodmingle.utils.emojipicker.utils.CACHE_FILE_NAME
import com.emc.moodmingle.utils.emojipicker.utils.EmojiDataSourceGoogle
import com.emc.moodmingle.utils.emojipicker.utils.NavigationBarPadding
import com.emc.moodmingle.utils.emojipicker.utils.defaultEmojiFontSize
import com.emc.moodmingle.utils.emojipicker.utils.defaultEmojiPadding
import com.emc.moodmingle.utils.emojipicker.utils.isEmojiCharacterRenderable
import com.vanniktech.emoji.google.GoogleEmojiProvider
import emoji.core.datasource.EmojiDataSourceImpl
import emoji.core.model.NetworkEmoji
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.ceil
import kotlin.math.floor

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
internal fun EmojiPickerList(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.background,

    groupTitleTextColor: Color = MaterialTheme.colorScheme.onBackground,
    groupTitleTextStyle: TextStyle = MaterialTheme.typography.headlineMedium,

    emojiFontSize: TextUnit = defaultEmojiFontSize,
    searchText: String = "",
    onEmojiClick: (emoji: Emoji) -> Unit,
    onEmojiLongClick: ((emoji: Emoji) -> Unit)? = null,
) {
    val context = LocalContext.current

    var emojisResult: EmojiPickerResult<List<Emoji>> by remember { mutableStateOf(value = EmojiPickerResult.Loading) }
    var columnCount by remember { mutableIntStateOf(value = 0) }
    var itemPadding by remember { mutableStateOf(value = 0.dp) }

    val firstEmoji: String? = remember(key1 = emojisResult) {
        (emojisResult as? EmojiPickerResult.Success<List<Emoji>>)?.data?.firstOrNull()?.character
    }

    val emojiWidth = rememberEmojiWidth(firstEmoji = firstEmoji, emojiFontSize = emojiFontSize)

    val filteredEmojis = remember(key1 = emojisResult, key2 = searchText) {
        when (emojisResult) {
            is EmojiPickerResult.Success -> {
                (emojisResult as EmojiPickerResult.Success<List<Emoji>>).data
                    .filter { emoji ->
                        if (searchText.isBlank()) true
                        else emoji.unicodeName.contains(searchText)
                    }
            }

            else -> emptyList()
        }
    }
    val uiItems: List<EmojiPickerItem> = remember(
        key1 = filteredEmojis,
        key2 = searchText,
        key3 = columnCount,
    ) {
        if (columnCount == 0) {
            emptyList()
        } else {
            filteredEmojis
                .groupBy { emoji -> emoji.group }
                .filter { (_, emojis) -> emojis.isNotEmpty() }
                .flatMap { (groupName, emojisInGroup) ->
                    val items = mutableListOf<EmojiPickerItem>()
                    items.add(element = EmojiPickerItem.EmojiGroupHeader(title = groupName))

                    emojisInGroup.chunked(size = columnCount)
                        .forEach { emojis ->
                            items.add(
                                element = EmojiPickerItem.EmojiGroupItems(emojis = emojis)
                            )
                        }

                    items
                }
        }
    }

    /*EmojiDataSourceImpl()
        .getAllEmojis(cacheFile = File(context.cacheDir, CACHE_FILE_NAME))
        .filter { isEmojiCharacterRenderable(emojiCharacter = it.character) }
        .map { networkEmoji -> Emoji(networkEmoji) }*/

    LaunchedEffect(key1 = Unit) {
        withContext(context = Dispatchers.IO) {
            withContext(context = Dispatchers.Main) {
                emojisResult = try {
                    EmojiPickerResult.Success(
                        data = EmojiDataSourceGoogle()
                            .getAllEmojis(cacheFile = File(context.cacheDir, CACHE_FILE_NAME))
//                            .filter { isEmojiCharacterRenderable(emojiCharacter = it.character) }
                            .map { networkEmoji -> Emoji(networkEmoji) }

                    )
                } catch (exception: Exception) {
                    EmojiPickerResult.Error(exception = exception)
                }
            }
        }
    }

    when (emojisResult) {
        is EmojiPickerResult.Error -> {
            EmojiPickerError()
            NavigationBarPadding()
        }

        is EmojiPickerResult.Loading -> {
            EmojiPickerLoading()
            NavigationBarPadding()
        }

        is EmojiPickerResult.Success -> {
            if (emojiWidth == null) {
                EmojiPickerEmpty()
                NavigationBarPadding()
            } else {
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    LaunchedEffect(key1 = maxWidth, key2 = emojiWidth) {
                        val (calculatedColumnCount, calculatedItemPadding) = getColumnData(
                            maxColumnWidth = maxWidth,
                            emojiWidth = emojiWidth,
                        )
                        columnCount = calculatedColumnCount
                        itemPadding = calculatedItemPadding
                    }

                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        uiItems.forEach { item ->
                            when (item) {
                                is EmojiPickerItem.EmojiGroupHeader -> {
                                    stickyHeader {
                                        EmojiPickerGroupTitle(
                                            backgroundColor = backgroundColor,
                                            titleTextColor = groupTitleTextColor,
                                            titleText = item.title,
                                            titleTextStyle = groupTitleTextStyle,
                                        )
                                    }
                                }

                                is EmojiPickerItem.EmojiGroupItems -> {
                                    item {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Start,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            item.emojis.forEach { emoji ->
                                                EmojiPickerEmoji(
                                                    modifier = Modifier.padding(horizontal = itemPadding),
                                                    emojiCharacter = emoji.character,
                                                    onClick = { onEmojiClick(emoji) },
                                                    onLongClick = { onEmojiLongClick?.invoke(emoji) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        item {
                            NavigationBarPadding()
                        }
                    }
                }
            }
        }
    }
}

private fun getColumnData(maxColumnWidth: Dp, emojiWidth: Dp): Pair<Int, Dp> {
    val emojiWidthPlusPadding = emojiWidth + (defaultEmojiPadding * 2)
    val columnCount = (maxColumnWidth / (emojiWidthPlusPadding)).toInt()
    val ceilEmojiWidth = ceil(emojiWidthPlusPadding.value).dp

    val itemPadding = floor(
        x = max(
            a = 0.dp,
            b = (maxColumnWidth - (ceilEmojiWidth * columnCount)) / (2 * columnCount)
        ).value
    ).dp

    return Pair(first = columnCount, second = itemPadding)
}

@Composable
private fun rememberEmojiWidth(firstEmoji: String?, emojiFontSize: TextUnit): Dp? {
    if (firstEmoji == null) return null

    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    return remember(key1 = firstEmoji, key2 = emojiFontSize) {
        with(receiver = density) {
            textMeasurer.measure(text = firstEmoji, style = TextStyle(fontSize = emojiFontSize))
                .size.width.toDp()
        }
    }
}