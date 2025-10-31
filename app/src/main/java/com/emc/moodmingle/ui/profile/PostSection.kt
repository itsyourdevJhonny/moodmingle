package com.emc.moodmingle.ui.profile

import androidx.annotation.DrawableRes
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.post.data.Post

fun getPosts(@DrawableRes avatarId: Int, avatar: String, username: String): List<Post> {
    val dummyPosts = List(10) { index ->
        val type = when {
            index < 10 -> "text"
            index < 20 -> "image"
            else -> "video"
        }

        val randomMood = listOf("Happy", "Calm", "Excited", "Grateful", "Anxious").random()
        val randomEmoji = listOf("😄", "😌", "🤗", "🙏", "😬").random()

        Post(
            name = username,
            avatar = avatarId,
            mood = randomMood,
            moodEmoji = randomEmoji,
            content = when (type) {
                "text" -> "Feeling $randomMood today! $randomEmoji"
                "image" -> "Captured this moment 🌄 #$randomMood"
                else -> "Vibing with this clip 🎥 #$randomMood"
            },
            timeAgo = "${(1..5).random()}h ago",
            likes = (1..50).random(),
            comments = (1..15).random(),
            shares = (1..10).random(),
            type = type,
            imageRes = R.drawable.happy_person,
            videoRes = R.raw.sample1,
            audioRes = R.raw.rob_deniel_ikaw_sana_and_nandito_ako
        )
    }

    return dummyPosts
}
