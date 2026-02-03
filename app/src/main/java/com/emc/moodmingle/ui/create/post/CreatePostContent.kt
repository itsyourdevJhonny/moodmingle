package com.emc.moodmingle.ui.create.post

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.api.soundcloud.viewmodel.SearchViewModel
import com.emc.moodmingle.data.firebase.model.post.normal.NormalPostEntity
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.ui.create.post.hashtag.extractHashtags
import com.emc.moodmingle.ui.post.PostHeader
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.ui.theme.UrlBackgroundColor
import com.emc.moodmingle.ui.theme.UrlTextColor
import com.emc.moodmingle.utils.components.ExpandableAnnotatedText
import com.emc.moodmingle.utils.media.MediaUtils
import com.emc.moodmingle.utils.text.toColor
import com.emc.moodmingle.utils.text.toFontFamily
import com.emc.moodmingle.utils.text.toTextAlign
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel

@Composable
fun CreatePostContent(
    paddingValues: PaddingValues,
    post: NormalPostEntity,
    onTypeSelected: (String) -> Unit
) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val user by userViewModel.loggedUser

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp)
            .padding(paddingValues)
            .background(Color.Black)
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        if (isPostNotChanged(post)) {
            PrimaryActions(onTypeSelected)
        } else {
            NormalPostContent(user, post)
        }
    }
}

private fun isPostNotChanged(post: NormalPostEntity): Boolean {
    return post.description.text.isEmpty() && post.mood.emoji.isEmpty() && post.urls.isEmpty()
}

@Composable
fun NormalPostContent(user: UserEntityFirebase?, post: NormalPostEntity) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<SearchViewModel>()
    val player = ExoPlayer.Builder(context).build()

    var isMuted by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .background(SecondaryDark)
            .animateContentSize()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            PostHeader(
                user,
                tagUserIds = post.taggedUserIds,
                primaryColor = Color.White,
                secondaryColor = Color.Black,
                mood = post.mood,
                location = post.location
            )

            MusicSection(post, player, viewModel)
            MentionSection(post)
            TextSection(post)
            HashtagSection(post)

            Box {
                MediaSection(post)
                MusicVolumeSection(post, isMuted, player) { isMuted = !isMuted }
            }
        }

        EventSection(post)
    }
}

@Composable
fun EventSection(post: NormalPostEntity) {
    post.linkMetadata?.let { metadata ->
        Column(modifier = Modifier.background(UrlBackgroundColor)) {
            Column(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${metadata.siteName}",
                    style = Typography.bodyLarge.copy(
                        color = UrlTextColor,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${metadata.title}",
                    style = Typography.bodyMedium.copy(color = Color.White),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(contentAlignment = Alignment.CenterEnd) {
                AsyncImage(
                    model = metadata.imageUrl,
                    contentDescription = "URL",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    contentScale = ContentScale.Crop
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "View Event",
                        style = Typography.bodyMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )

                    Box(
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Arrow Right",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MentionSection(post: NormalPostEntity) {
    if (post.mentionedUserIds.isNotEmpty()) {
        val userViewModel = hiltViewModel<FirebaseUserViewModel>()

        val mentionUsernames = post.mentionedUserIds.map { userId ->
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

@Composable
fun TextSection(post: NormalPostEntity) {
    if (post.description.text.isNotEmpty()) {
        ExpandableAnnotatedText(
            fullText = post.description.text,
            style = Typography.bodyLarge.copy(
                color = post.description.color.toColor(),
                textAlign = post.description.align.toTextAlign(),
                fontFamily = post.description.font.toFontFamily()
            ),
            minLines = 2
        )
    }
}

@Composable
fun HashtagSection(post: NormalPostEntity) {
    val hashtags = extractHashtags(post.hashtag)

    if (hashtags.isNotEmpty()) {
        ExpandableAnnotatedText(
            fullText = hashtags.joinToString(" ") { "#${it.replace(" ", "")}" },
            style = Typography.bodyLarge.copy(lineHeight = 20.sp),
            minLines = 1
        )
    }
}

@Composable
fun MediaSection(post: NormalPostEntity) {
    if (post.urls.isNotEmpty()) {
        val mediaUtils = MediaUtils()
        val mediaUris = post.urls.map { it.toUri() }

        when (mediaUris.size) {
            1 -> mediaUtils.SingleMedia(mediaUris[0], 406.dp)
            2 -> mediaUtils.DoubleMedia(mediaUris, 200.dp, 172.dp)
            3 -> mediaUtils.TripleMedia(mediaUris, 300.dp, 200.dp, 150.dp)
            else -> mediaUtils.MultipleMedia(mediaUris, 172.dp)
        }
    }
}

@Composable
fun MusicSection(post: NormalPostEntity, player: ExoPlayer, viewModel: SearchViewModel) {
    post.musicTrack?.let { track ->
        var musicUrl by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(Unit) {
            musicUrl = viewModel.getPlayableUrlFromServer(track.id)
        }

        musicUrl?.let { url ->
            player.setMediaItem(MediaItem.fromUri(url))
            player.prepare()
            player.playWhenReady = true
            player.repeatMode = Player.REPEAT_MODE_ONE

            DisposableEffect(Unit) { onDispose { player.release() } }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.music_note),
                contentDescription = "Music",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )

            Text(text = track.artist, color = Color.White, fontWeight = FontWeight.Black)

            Text(text = "•", color = GrayTextColor)

            Text(
                text = track.title,
                style = Typography.bodyMedium.copy(
                    color = Color.Green.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Black
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun BoxScope.MusicVolumeSection(
    post: NormalPostEntity,
    isMuted: Boolean,
    player: ExoPlayer,
    onVolumeChanged: () -> Unit
) {
    post.musicTrack?.let {
        Box(
            modifier = Modifier
                .clickable {
                    onVolumeChanged()
                    player.volume = if (isMuted) 0f else 1f
                }
                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                .padding(8.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(
                painter = painterResource(if (isMuted) R.drawable.pause_sound else R.drawable.play_sound),
                contentDescription = "Play",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun PrimaryActions(onTypeSelected: (String) -> Unit) {
    val actions = listOf(
        R.drawable.text_style to "Text",
        R.drawable.image_video to "Media",
        R.drawable.mood to "Mood"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        actions.forEach { (actionIcon, label) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .background(BrushPrimaryGradient, RoundedCornerShape(8.dp))
                        .clickable { onTypeSelected(label.lowercase()) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(actionIcon),
                        contentDescription = "Action",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(text = label, style = Typography.bodyMedium.copy(color = GrayTextColor))
            }
        }
    }
}