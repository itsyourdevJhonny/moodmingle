package com.emc.moodmingle.data.firebase.model.post.dailymood

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import com.emc.moodmingle.api.nominatim.NominatimPlace
import com.emc.moodmingle.data.firebase.model.post.normal.PostDescription
import com.emc.moodmingle.data.firebase.model.remix.Mood
import com.emc.moodmingle.utils.color.toHex
import com.emc.moodmingle.utils.media.image.ImageFilterType
import com.emc.moodmingle.utils.text.toFontName
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

data class DailyMoodEntity(
    val id: String = "",
    val mood: Mood = Mood(),
    val description: PostDescription? = null,
    val media: DailyMoodMedia = DailyMoodMedia(),
    val musicTrack: MusicTrack? = null,
    val location: NominatimPlace? = null,
    val text: DailyMoodText = DailyMoodText(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = 0
)

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
    val offsetY: Float = 0f
) : Parcelable

enum class TextStyle {
    NORMAL,
    WITH_BACKGROUND,
    WITHOUT_BACKGROUND
}

@Parcelize
data class DailyMoodMedia(
    val urls: List<String> = emptyList(),
    val imageFilterName: String = ImageFilterType.NORMAL.name,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f
) : Parcelable

@Parcelize
data class MusicTrack(
    val id: Long,
    val title: String,
    val artist: String,
    @Json(name = "artworkUrl") val artworkUrl: String?,
    @Json(name = "streamUrl") val streamUrl: String?,
    val duration: Long
) : Parcelable
