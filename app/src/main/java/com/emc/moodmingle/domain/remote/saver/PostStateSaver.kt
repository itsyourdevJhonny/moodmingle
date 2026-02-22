package com.emc.moodmingle.domain.remote.saver

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import com.emc.moodmingle.domain.remote.model.post.normal.PostEntityFirebase

// SAVER FOR MutableState<PostEntityFirebase?>
val PostStateSaver: Saver<MutableState<PostEntityFirebase?>, List<Any?>> = Saver(
    save = { state ->
        val p = state.value
        if (p == null) emptyList()
        else listOf(
            p.id,
            p.userId,
            p.username,
            p.avatarUrl,
            p.mood,
            p.moodEmoji,
            p.hashtag,
            p.caption,
            p.description,
            p.timeAgo,
            p.comments,
            p.likes,
            p.shares,
            p.type,
            ArrayList(p.urls)
        )
    },
    restore = { saved ->
        if (saved.isEmpty()) mutableStateOf(null)
        else {
            val urlsAny = saved[14]
            val urlsList: List<String> = when (urlsAny) {
                is List<*> -> urlsAny.filterNotNull().map { it as String }
//                is ArrayList<*> -> urlsAny.filterNotNull().map { it as String }
                else -> emptyList()
            }

            val restored = PostEntityFirebase(
                id = saved[0] as String,
                userId = saved[1] as String,
                username = saved[2] as String,
                avatarUrl = saved[3] as String,
                mood = saved[4] as String,
                moodEmoji = saved[5] as String,
                hashtag = saved[6] as String,
                caption = saved[7] as String,
                description = saved[8] as String,
                timeAgo = (saved[9] as Number).toLong(),
                comments = (saved[10] as Number).toLong(),
                likes = (saved[11] as Number).toLong(),
                shares = (saved[12] as Number).toLong(),
                type = saved[13] as String,
                urls = urlsList
            )
            mutableStateOf(restored)
        }
    }
)