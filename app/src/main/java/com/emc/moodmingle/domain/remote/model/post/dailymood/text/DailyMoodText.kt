package com.emc.moodmingle.domain.remote.model.post.dailymood.text

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import com.emc.moodmingle.utils.color.toHex
import com.emc.moodmingle.utils.text.toFontName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DailyMoodText(
    val description: String = "",
    val font: String = FontFamily.Default.toFontName(),
    val color: String = Color.White.toHex(),
    val align: String = TextAlign.Unspecified.toString(),
    val style: TextStyle = TextStyle.NORMAL,
    val hashtag: String = "",
    val mentions: List<String> = emptyList(),
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
) : Parcelable

enum class TextStyle {
    NORMAL,
    WITH_BACKGROUND,
    WITHOUT_BACKGROUND
}