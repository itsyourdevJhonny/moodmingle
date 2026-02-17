package com.emc.moodmingle.data.firebase.model.post.dailymood

import android.os.Parcelable
import com.emc.moodmingle.api.nominatim.NominatimPlace
import com.emc.moodmingle.data.firebase.model.post.dailymood.gif.Gif
import com.emc.moodmingle.data.firebase.model.post.dailymood.media.DailyMoodMedia
import com.emc.moodmingle.data.firebase.model.post.dailymood.music.MusicTrack
import com.emc.moodmingle.data.firebase.model.post.dailymood.settings.DailyMoodSettings
import com.emc.moodmingle.data.firebase.model.post.dailymood.text.DailyMoodText
import com.emc.moodmingle.data.firebase.model.post.normal.PostDescription
import com.emc.moodmingle.data.firebase.model.remix.Mood
import kotlinx.parcelize.Parcelize

data class DailyMoodEntity(
    val id: String = "",
    val mood: Mood = Mood(),
    val description: PostDescription? = null,
    val media: DailyMoodMedia = DailyMoodMedia(),
    val gif: Gif = Gif(),
    val musicTrack: MusicTrack? = null,
    val location: NominatimPlace? = null,
    val text: DailyMoodText = DailyMoodText(),
    val audience: DailyMoodAudience = DailyMoodAudience(),
    val settings: DailyMoodSettings = DailyMoodSettings(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = 0,
)

@Parcelize
data class DailyMoodAudience(
    val type: AudienceType = AudienceType.PUBLIC,
    val selectedUsers: List<String> = emptyList(),
) : Parcelable

enum class AudienceType {
    PUBLIC,
    PRIVATE,
    FOLLOWERS,
    SUPPORTERS,
    CUSTOM
}