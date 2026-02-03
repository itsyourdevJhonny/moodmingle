package com.emc.moodmingle.data.firebase.saver

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
val UserStateSaver: Saver<MutableState<UserEntityFirebase?>, List<Any?>> = Saver(
    save = { state ->
        val u = state.value
        if (u == null) emptyList()
        else listOf(
            u.uid,
            u.username,
            u.email,
            u.password,
            u.avatarUrl,
            u.bio,
            u.joinedDate
        )
    },
    restore = { saved ->
        if (saved.isEmpty()) mutableStateOf(null)
        else {
            val restored = UserEntityFirebase(
                uid = saved[0] as String,
                username = saved[1] as String,
                email = saved[2] as String,
                password = saved[3] as String,
                avatarUrl = saved[4] as String,
                bio = saved[5] as String,
                joinedDate = saved[6] as String
            )
            mutableStateOf(restored)
        }
    }
)
