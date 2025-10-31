package com.emc.moodmingle.ui.post.data

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.emc.moodmingle.R

data class Post(
    val name: String,
    @DrawableRes val avatar: Int,
    val mood: String,
    val moodEmoji: String,
    val content: String,
    val timeAgo: String,
    val likes: Int,
    val comments: Int,
    val shares: Int,
    val type: String,
    @DrawableRes val imageRes: Int,
    val videoRes: Int? = null,
    @RawRes val audioRes: Int
)

@Composable
fun dummyPosts(): List<Post> {
    val imageList = getImageList()
    val videoList = getVideoList()
    val audioList = getAudioList()

    val posts = remember {
        List(40) { index ->
            val type = when {
                index < 10 -> "text"
                index < 20 -> "image"
                index < 30 -> "video"
                else -> "audio"
            }

            val randomMood = listOf("Happy", "Calm", "Excited", "Grateful", "Anxious").random()
            val randomEmoji = listOf("😄", "😌", "🤗", "🙏", "😬").random()

            val randomImage = imageList.random()
            val randomVideo = videoList.random()
            val randomAudio = audioList.random()

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
                videoRes = randomVideo,
                audioRes = randomAudio
            )
        }.shuffled()
    }

    return posts
}

fun getImageList(): List<Int> {
    return listOf(
        R.drawable.happy_person,
        R.drawable.beach_vibes,
        R.drawable.sunset_chill,
        R.drawable.city_walk,
        R.drawable.mountain_hike
    )
}

fun getVideoList(): List<Int> {
    return listOf(
        R.raw.sample1,
        R.raw.sample2,
        R.raw.sample3,
        R.raw.sample4,
        R.raw.sample5
    )
}

fun getAudioList(): List<Int> {
    return listOf(
        R.raw.libu_libong_buwan_uuwian_kyle_raphael,
        R.raw.multo_cup_of_joe,
        R.raw.rob_deniel_ikaw_sana_and_nandito_ako,
        R.raw.tothapi_panata,
        R.raw.zack_tabudlo_pulso
    )
}