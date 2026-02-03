package com.emc.moodmingle.data.firebase.model.user

import com.google.firebase.firestore.IgnoreExtraProperties
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@IgnoreExtraProperties
data class UserEntityFirebase(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val avatarUrl: String = "",
    val bio: String = "",
    val joinedDate: String = getCurrentDate(),
    val private: Boolean = false,
    val moodNoteDisabled: Boolean = false,
    val verified: Boolean = false,
    val chatDisabled: Boolean = false,

    val emailEncrypt: Map<String, Boolean> = mapOf("value" to false),
    val usernameEncrypt: Map<String, Boolean> = mapOf("value" to false),
    val passwordEncrypt: Map<String, Boolean> = mapOf("value" to false),
    val bioEncrypt: Map<String, Boolean> = mapOf("value" to false),
    val avatarEncrypt: Map<String, Boolean> = mapOf("value" to false),

    val hashtagEncrypt: Map<String, Boolean> = mapOf("value" to false),
    val captionEncrypt: Map<String, Boolean> = mapOf("value" to false),
    val descriptionEncrypt: Map<String, Boolean> = mapOf("value" to false),
    val moodTextEncrypt: Map<String, Boolean> = mapOf("value" to false),
    val moodEmojiEncrypt: Map<String, Boolean> = mapOf("value" to false),

    val followerIds: List<String> = emptyList(),
    val followingIds: List<String> = emptyList(),
    val supporterIds: List<String> = emptyList(),

    val reposts: List<Repost> = emptyList(),

    val hiddenVideoUrls: List<String> = emptyList(),
    val savedVideoUrls: List<String> = emptyList(),
    val favoriteVideoUrls: List<String> = emptyList(),
)

fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return dateFormat.format(Date())
}

