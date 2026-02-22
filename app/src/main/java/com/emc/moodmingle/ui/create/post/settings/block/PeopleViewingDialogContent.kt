package com.emc.moodmingle.ui.create.post.settings.block

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.ui.settings.saved.utils.EmptyComponent
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.rememberUsersByIds
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel

@Composable
fun PeopleViewingDialogContent(
    paddingValues: PaddingValues,
    userIds: List<String>,
    onUsersBlocked: (List<String>) -> Unit,
    onShowSelectUserDialog: (Boolean) -> Unit
) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val users by rememberUsersByIds(userIds, userViewModel)

    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Description()

            SelectAndRemoveAllButton(userIds, onUsersBlocked, onShowSelectUserDialog)

            HorizontalDivider(
                thickness = 0.5.dp,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            if (users.isNotEmpty()) {
                LazyColumn {
                    items(users, key = { it.uid }) { user ->
                        UserItem(user, onClick = { onUsersBlocked(userIds - user.uid) })
                    }
                }
            }
        }

        EmptyIndicator(userIds)
    }
}

@Composable
private fun Description() {
    Text(
        text = "People won't be notified when you block them from viewing your post. You can still unblock them anytime after you posting.",
        style = Typography.bodyMedium.copy(
            color = GrayTextColor,
            textAlign = TextAlign.Center
        ),
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

@Composable
private fun SelectAndRemoveAllButton(
    userIds: List<String>,
    onUsersBlocked: (List<String>) -> Unit,
    onShowSelectUserDialog: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (userIds.size > 1) Arrangement.SpaceAround else Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .clickable { onShowSelectUserDialog(true) }
                .background(BrushPrimaryGradient, CircleShape)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            Text(text = "Select People", color = Color.White, fontWeight = FontWeight.Bold)
        }

        if (userIds.size > 1) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onUsersBlocked(emptyList()) }
                    .padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.remove),
                    contentDescription = "Remove All",
                    tint = Color.Red,
                    modifier = Modifier.size(18.dp)
                )

                Text(text = " Remove All", style = Typography.bodyMedium.copy(color = Color.White))
            }
        }
    }
}

@Composable
private fun EmptyIndicator(userIds: List<String>) {
    AnimatedVisibility(visible = userIds.isEmpty(), enter = fadeIn(), exit = fadeOut()) {
        EmptyComponent(R.drawable.block_user, "No blocked people yet.")
    }
}

@Composable
fun LazyItemScope.UserItem(user: UserEntityFirebase, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .animateItem()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Text(text = user.username, color = Color.White, fontWeight = FontWeight.Bold)
        }

        Box(
            modifier = Modifier
                .clickable { onClick() }
                .background(PrimaryDark, CircleShape)
                .padding(8.dp)
        ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Red)
        }
    }
}