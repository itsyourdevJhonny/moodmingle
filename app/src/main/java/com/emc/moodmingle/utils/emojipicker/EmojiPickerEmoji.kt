package com.emc.moodmingle.utils.emojipicker

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.EmojiSupportMatch
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.emoji2.text.EmojiCompat
import com.emc.moodmingle.utils.emojipicker.utils.defaultEmojiFontSize
import com.emc.moodmingle.utils.emojipicker.utils.defaultEmojiPadding
import com.emc.moodmingle.utils.emojipicker.utils.defaultEmojiUIShape
import com.vanniktech.emoji.emojiInformation

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmojiPickerEmoji(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    backgroundColor: Color = Transparent,
    loadingSize: Dp = 38.dp,
    padding: Dp = defaultEmojiPadding,
    shape: Shape = defaultEmojiUIShape,
    emojiCharacter: String,
    fontSize: TextUnit = defaultEmojiFontSize,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
) {
    if (isLoading) {
        EmojiLoading(modifier = modifier, loadingSize = loadingSize, shape = shape)
    } else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .clip(shape = shape)
                .combinedClickable(onClick = onClick, onLongClick = onLongClick)
                .background(backgroundColor)
                .padding(padding)
        ) {
            Text(
                text = EmojiCompat.get().process(emojiCharacter).toString(),
                style = TextStyle(
                    fontSize = fontSize,
                    platformStyle = PlatformTextStyle(emojiSupportMatch = EmojiSupportMatch.None),
                    textAlign = TextAlign.Center,
                )
            )
        }
    }
}

@Composable
private fun EmojiLoading(modifier: Modifier, loadingSize: Dp, shape: Shape) {
    Box(
        modifier = modifier
            .size(loadingSize)
            .clip(shape)
    )
}