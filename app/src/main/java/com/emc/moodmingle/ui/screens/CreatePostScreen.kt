package com.emc.moodmingle.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.data.firebase.model.post.normal.NormalPostEntity
import com.emc.moodmingle.data.firebase.model.post.settings.PostSettings
import com.emc.moodmingle.service.post.NormalPostService
import com.emc.moodmingle.ui.create.AllMediaGallery
import com.emc.moodmingle.ui.create.post.CreatePostContent
import com.emc.moodmingle.ui.create.post.CreatePostFooter
import com.emc.moodmingle.ui.create.post.CreatePostHeader
import com.emc.moodmingle.ui.create.post.dialogs.CreatePostEventDialog
import com.emc.moodmingle.ui.create.post.dialogs.CreatePostHashtagDialog
import com.emc.moodmingle.ui.create.post.dialogs.CreatePostLocationDialog
import com.emc.moodmingle.ui.create.post.dialogs.CreatePostMentionDialog
import com.emc.moodmingle.ui.create.post.dialogs.CreatePostSettingsDialog
import com.emc.moodmingle.ui.create.post.dialogs.CreatePostTagDialog
import com.emc.moodmingle.ui.create.post.dialogs.CreatePostTextDialog
import com.emc.moodmingle.ui.music.MusicPicker
import com.emc.moodmingle.ui.remix.MoodPickerDialog
import com.emc.moodmingle.utils.color.toHex
import com.emc.moodmingle.utils.components.DiscardDialog
import com.emc.moodmingle.utils.text.toColor
import com.emc.moodmingle.utils.text.toFontFamily
import com.emc.moodmingle.utils.text.toFontName
import com.emc.moodmingle.utils.text.toTextAlign
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel

@Composable
fun CreatePostScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val currentUser by userViewModel.loggedUser

    var type by remember { mutableStateOf("") }

    var post by remember { mutableStateOf(NormalPostEntity()) }
    var hashtag by remember { mutableStateOf(TextFieldValue("#", TextRange(1))) }

    // Settings
    var postSettings by remember { mutableStateOf(PostSettings()) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    // Discard
    var showDiscardDialog by remember { mutableStateOf(false) }

    BackHandler {
        when {
            type.isNotBlank() -> type = ""
            showSettingsDialog -> showSettingsDialog = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Scaffold(
            containerColor = Color.Black,
            topBar = {
                CreatePostHeader(
                    currentUser,
                    post,
                    onSettingsOpened = { showSettingsDialog = it },
                    onPost = { performUploadOperation(context, post, onBack) },
                    onBack = { if (isPostChanged(post)) showDiscardDialog = true else onBack() }
                )
            },
            bottomBar = { CreatePostFooter(post, onTypeSelected = { type = it }) }
        ) { paddingValues ->
            CreatePostContent(paddingValues, post, onTypeSelected = { type = it })
        }

        when (type) {
            "text" -> {
                CreatePostTextDialog(
                    currentText = post.description.text,
                    currentFont = post.description.font.toFontFamily(),
                    currentColor = post.description.color.toColor(),
                    currentAlign = post.description.align.toTextAlign(),
                    onTextChanged = {
                        post = post.copy(description = post.description.copy(text = it))
                    },
                    onFontSelected = {
                        post =
                            post.copy(description = post.description.copy(font = it.toFontName()))
                    },
                    onColorSelected = {
                        post = post.copy(description = post.description.copy(color = it.toHex()))
                    },
                    onAlignSelected = {
                        post = post.copy(description = post.description.copy(align = it.toString()))
                    },
                    onDismiss = { type = "" }
                )
            }

            "hashtag" -> {
                CreatePostHashtagDialog(
                    hashtag = hashtag,
                    onHashtagChange = {
                        hashtag = it
                        post = post.copy(hashtag = it.text)
                    },
                    onDismiss = { type = "" }
                )
            }

            "mention" -> {
                CreatePostMentionDialog(
                    mentionUserIds = post.mentionedUserIds,
                    onMentionedUsers = { post = post.copy(mentionedUserIds = it) },
                    onDismiss = { type = "" }
                )
            }

            "tag" -> {
                CreatePostTagDialog(
                    tagUserIds = post.taggedUserIds,
                    onTaggedUsers = { post = post.copy(taggedUserIds = it) },
                    onDismiss = { type = "" }
                )
            }

            "location" -> {
                CreatePostLocationDialog(
                    location = post.location,
                    onSelectedLocation = { post = post.copy(location = it) },
                    onDismiss = { type = "" }
                )
            }

            "event" -> {
                CreatePostEventDialog(
                    metadata = post.linkMetadata,
                    onEventSelected = { post = post.copy(linkMetadata = it) },
                    onDismiss = { type = "" }
                )
            }

            "mood" -> {
                MoodPickerDialog(
                    selectedMood = post.mood,
                    onDismiss = { type = "" },
                    onSelectedMood = { post = post.copy(mood = it) }
                )
            }

            "media" -> {
                AllMediaGallery(
                    mediaUris = post.urls.map { it.toUri() },
                    onSelectedType = {},
                    onUploadedUri = { uris -> post = post.copy(urls = uris.map { it.toString() }) },
                    onDismiss = { type = "" }
                )
            }

            "music" -> {
                MusicPicker(
                    onMusicSelected = { post = post.copy(musicTrack = it) },
                    onDismiss = { type = "" },
                )
            }
        }
    }

    when {
        showDiscardDialog -> {
            DiscardDialog(
                title = "Discard Changes?",
                text = "Unsave changes detected, are you sure you want to discard it?",
                confirmText = "Discard",
                cancelText = "Add to Draft",
                onConfirm = { showDiscardDialog = false; onBack() },
                onCancel = { showDiscardDialog = false; onBack() }
            )
        }

        showSettingsDialog -> {
            CreatePostSettingsDialog(
                postSettings,
                onSettingsCreated = { postSettings = it },
                onDismiss = { showSettingsDialog = false }
            )
        }
    }
}

private fun performUploadOperation(context: Context, post: NormalPostEntity, onBack: () -> Unit) {
    val uploadIntent = Intent(context, NormalPostService::class.java).apply {
        action = NormalPostService.ACTION_UPLOAD
        putExtra(NormalPostService.EXTRA_NORMAL_POST, post)
    }

    context.startForegroundService(uploadIntent)
    Toast.makeText(context, "Uploading post...", Toast.LENGTH_SHORT).show()
    onBack()
}

private fun isPostChanged(post: NormalPostEntity): Boolean {
    return post.mood.emoji.isNotBlank() ||
            post.description.text.isNotBlank() ||
            post.hashtag.isNotBlank() && post.hashtag != "#" ||
            post.urls.isNotEmpty() ||
            post.mentionedUserIds.isNotEmpty() ||
            post.taggedUserIds.isNotEmpty() ||
            post.location.isNotEmpty() ||
            post.linkMetadata != null
}

fun getMoods(): List<Pair<String, String>> {
    return listOf(
        "😀" to "Happy",
        "😌" to "Calm",
        "🤗" to "Excited",
        "🙏" to "Grateful",
        "😬" to "Anxious",
        "😢" to "Sad",
        "😠" to "Angry",
        "😴" to "Sleepy",
        "🤔" to "Thoughtful",
        "😳" to "Embarrassed",
        "😇" to "Content",
        "🤩" to "Amazed",
        "🥰" to "Loved",
        "😭" to "Heartbroken",
        "😎" to "Confident",
        "😕" to "Confused",
        "😮" to "Surprised",
        "😒" to "Bored",
        "😤" to "Frustrated",
        "🤒" to "Sick",
        "🤪" to "Playful",
        "😞" to "Disappointed",
        "🥳" to "Cheerful",
        "🤯" to "Overwhelmed",
        "🥺" to "Hopeful",
        "😔" to "Lonely",
        "😱" to "Scared",
        "🤫" to "Secretive",
        "😐" to "Neutral",
        "🫠" to "Exhausted"
    )
}

fun getEmojiByEmotion(): Map<String, String> {
    return mapOf(
        "Happy" to "😀",
        "Calm" to "😌",
        "Excited" to "🤗",
        "Grateful" to "🙏",
        "Anxious" to "😬",
        "Sad" to "😢",
        "Angry" to "😠",
        "Sleepy" to "😴",
        "Thoughtful" to "🤔",
        "Embarrassed" to "😳",
        "Content" to "😇",
        "Amazed" to "🤩",
        "Loved" to "🥰",
        "Heartbroken" to "😭",
        "Confident" to "😎",
        "Confused" to "😕",
        "Surprised" to "😮",
        "Bored" to "😒",
        "Frustrated" to "😤",
        "Sick" to "🤒",
        "Playful" to "🤪",
        "Disappointed" to "😞",
        "Cheerful" to "🥳",
        "Overwhelmed" to "🤯",
        "Hopeful" to "🥺",
        "Lonely" to "😔",
        "Scared" to "😱",
        "Secretive" to "🤫",
        "Neutral" to "😐",
        "Exhausted" to "🫠"
    )
}