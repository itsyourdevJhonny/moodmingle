package com.emc.moodmingle.utils.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel

@Composable
fun AnnotatedMention(mentions: List<String>) {
    if (mentions.isNotEmpty()) {
        val userViewModel = hiltViewModel<FirebaseUserViewModel>()

        val mentionUsernames = mentions.map { userId ->
            val user by remember(userId) {
                userViewModel.getUserById(userId)
            }.collectAsState(initial = null)

            user?.username ?: ""
        }

        ExpandableAnnotatedText(
            fullText = mentionUsernames.joinToString(", ") { "@$it" },
            minLines = 1
        )
    }
}