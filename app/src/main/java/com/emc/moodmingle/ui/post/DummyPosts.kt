package com.emc.moodmingle.ui.post

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.screens.Post

@Composable
fun dummyPosts(): List<Post> {
    val imageList = listOf(
        R.drawable.happy_person,
        R.drawable.beach_vibes,
        R.drawable.sunset_chill,
        R.drawable.city_walk,
        R.drawable.mountain_hike
    )

    val videoList = listOf(
        R.raw.sample1,
        R.raw.sample2,
        R.raw.sample3,
        R.raw.sample4,
        R.raw.sample5
    )

    val posts = remember {
        List(30) { index ->
            val type = when {
                index < 10 -> "text"
                index < 20 -> "image"
                else -> "video"
            }

            val randomMood = listOf("Happy", "Calm", "Excited", "Grateful", "Anxious").random()
            val randomEmoji = listOf("😄", "😌", "🤗", "🙏", "😬").random()
            val randomImage = imageList.random()
            val randomVideo = videoList.random()

            Post(
                name = "User ${index + 1}",
                avatar = listOf(
                    R.raw.boy1,
                    R.raw.boy2,
                    R.raw.boy3,
                    R.raw.girl1,
                    R.raw.girl2,
                    R.raw.girl3
                ).random(),
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
                imageRes = randomImage,
                videoRes = randomVideo
            )
        }.shuffled()
    }

    return posts
}