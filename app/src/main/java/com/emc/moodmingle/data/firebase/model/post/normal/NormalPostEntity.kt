package com.emc.moodmingle.data.firebase.model.post.normal

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import com.emc.moodmingle.api.soundcloud.TrackResponse
import com.emc.moodmingle.data.firebase.model.post.settings.PostSettings
import com.emc.moodmingle.data.firebase.model.remix.Mood
import com.emc.moodmingle.ui.create.post.dialogs.LinkMetadata
import com.emc.moodmingle.utils.color.toHex
import com.emc.moodmingle.utils.font.FontUtils
import kotlinx.parcelize.Parcelize

@Parcelize
data class NormalPostEntity(
    val id: String = "",
    val userId: String = "",
    val description: PostDescription = PostDescription(),
    val hashtag: String = "#",
    val mood: Mood = Mood(),
    val urls: List<String> = emptyList(),
    val musicTrack: TrackResponse? = null,
    val mentionedUserIds: List<String> = emptyList(),
    val taggedUserIds: List<String> = emptyList(),
    val location: String = "",
    val linkMetadata: LinkMetadata? = null,
    val settings: PostSettings = PostSettings(),
    val timestamp: Long = System.currentTimeMillis(),
    val reactorIds: List<String> = emptyList(),
    val commenterIds: List<String> = emptyList(),
    val sharerIds: List<String> = emptyList(),
    val saverIds: List<String> = emptyList(),
    val favoriterIds: List<String> = emptyList(),
    val hiderIds: List<String> = emptyList(),
) : Parcelable

@Parcelize
data class PostDescription(
    val text: String = "",
    val font: String = FontUtils.getFontName(FontFamily.Default),
    val color: String = Color.White.toHex(),
    val align: String = TextAlign.Unspecified.toString(),
) : Parcelable
