package com.emc.moodmingle.ui.video.comment.more.secondary.trigger

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.data.firebase.model.video.VideoComment
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.utils.modifier.grayCircleBorder
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel

@Composable
fun TriggerFirstPage(comment: VideoComment) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val commenter by remember(comment.commenterId) {
        userViewModel.getUserById(comment.commenterId)
    }.collectAsState(initial = null)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TriggerIcon()
            Subtitle()
            WhatHappensNextInfo()
            CommenterToTrigger(commenter)
        }
    }
}

@Composable
private fun TriggerIcon() {
    Box(
        modifier = Modifier
            .size(78.dp)
            .background(SecondaryDark, CircleShape)
            .grayCircleBorder(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.triggering),
            contentDescription = "Triggering",
            tint = Color.Red,
            modifier = Modifier.size(48.dp)
        )
    }
}

@Composable
private fun WhatHappensNextInfo() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "What happens next?",
            style = Typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
        )
        BulletText(text = "Our moderation system will review the comment and take appropriate action.")
        BulletText(text = "The comment maybe limited or removed.")
        BulletText(text = "Your identity will not be shared with the people and comment author.")
    }
}

@Composable
private fun CommenterToTrigger(commenter: UserEntityFirebase?) {
    TriggeringLabel()

    Card(
        colors = CardDefaults.cardColors(containerColor = SecondaryDark),
        shape = CircleShape,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            CommenterAvatar(commenter)
            CommenterUsernameAndTimestamp(commenter)
            VerifiedIcon(commenter)
        }
    }
}

@Composable
private fun TriggeringLabel() {
    Text(
        text = "You are triggering a comment from:",
        style = Typography.bodyMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    )
}

@Composable
private fun CommenterAvatar(commenter: UserEntityFirebase?) {
    AsyncImage(
        model = commenter?.avatarUrl,
        contentDescription = "Avatar",
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun CommenterUsernameAndTimestamp(commenter: UserEntityFirebase?) {
    Text(
        text = "${commenter?.username}",
        style = Typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Black),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.widthIn(max = 230.dp)
    )
}

@Composable
private fun VerifiedIcon(commenter: UserEntityFirebase?) {
    if (commenter?.verified == true) {
        Icon(
            painter = painterResource(R.drawable.verified),
            contentDescription = "Verified",
            modifier = Modifier
                .size(20.dp)
                .drawGradient()
        )
    }
}

@Composable
fun BulletText(text: String) {
    Text(
        text = buildAnnotatedString {
            append("• ")
            append(text)
        },
        style = Typography.bodyMedium.copy(color = Color.White),
        modifier = Modifier
    )
}

@Composable
private fun Subtitle() {
    Text(
        text = "Help us keep the community safe and peaceful by flagging comment as triggering or harmful.",
        style = Typography.bodyMedium,
        textAlign = TextAlign.Center
    )
}