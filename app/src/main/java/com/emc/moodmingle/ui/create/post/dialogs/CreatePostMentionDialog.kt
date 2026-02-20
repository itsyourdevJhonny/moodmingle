package com.emc.moodmingle.ui.create.post.dialogs

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.ui.create.post.CreatePostDialogHeader
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.MentionTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.utils.modifier.gradientCircleBorder
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel

@Composable
fun CreatePostMentionDialog(
    mentionUserIds: List<String>,
    onMentionedUsers: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    val selectedUserIds = remember { mutableStateListOf<String>() }

    if (mentionUserIds.isNotEmpty()) selectedUserIds.addAll(mentionUserIds)

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            CreatePostDialogHeader(
                label = "Mention People",
                enabled = selectedUserIds.isNotEmpty(),
                onOkay = {
                    onMentionedUsers(selectedUserIds.toList())
                    onDismiss()
                },
                onBack = onDismiss
            )
        }
    ) { paddingValues ->
        MentionDialogContent(paddingValues, selectedUserIds)
    }
}

@Composable
private fun MentionDialogContent(
    paddingValues: PaddingValues,
    selectedUserIds: SnapshotStateList<String>
) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()

    var value by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(paddingValues),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SearchField(value, onValueChange = { value = it })

        if (value.isNotBlank()) {
            SearchResult(userViewModel, value, selectedUserIds) { value = it }
        } else {
            if (selectedUserIds.isNotEmpty()) {
                SelectedResult(selectedUserIds, userViewModel)
            } else {
                Suggestions(selectedUserIds, userViewModel)
            }
        }
    }
}

@Composable
private fun Suggestions(
    selectedUserIds: SnapshotStateList<String>,
    userViewModel: FirebaseUserViewModel
) {
    val users by userViewModel.pagedUsers.collectAsState()

    val listState = rememberLazyListState()

    LaunchedEffect(Unit) { userViewModel.loadNextUsersPage() }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex == users.lastIndex) {
                    userViewModel.loadNextUsersPage()
                }
            }
    }

    LazyColumn(state = listState) {
        items(users) { user -> SuggestionUserItem(user, selectedUserIds) }

        if (users.isEmpty()) {
            item { CircularProgressIndicator(modifier = Modifier.padding(16.dp)) }
        }
    }
}

@Composable
private fun SuggestionUserItem(
    user: UserEntityFirebase,
    selectedUserIds: SnapshotStateList<String>
) {
    val debouncedClick = rememberDebouncedClick {
        if (!selectedUserIds.contains(user.uid)) {
            selectedUserIds.add(user.uid)
        }
    }

    SearchUserItem(
        icon = R.drawable.mention,
        MentionTextColor,
        user,
        globalClickEnabled = true,
        onClick = debouncedClick
    )
}

@Composable
private fun rememberDebouncedClick(
    debounceMillis: Long = 400L,
    onClick: () -> Unit
): () -> Unit {
    var lastClickTime by remember { mutableLongStateOf(0L) }

    return {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > debounceMillis) {
            lastClickTime = currentTime
            onClick()
        }
    }
}

@Composable
private fun SearchResult(
    userViewModel: FirebaseUserViewModel,
    value: String,
    selectedUserIds: SnapshotStateList<String>,
    onValueChange: (String) -> Unit
) {
    val searchedUsers by remember(value) {
        userViewModel.searchByUsername(value)
    }.collectAsState(initial = emptyList())

    LazyColumn {
        items(searchedUsers.filterNot { selectedUserIds.contains(it.uid) }) { user ->
            SearchUserItem(
                icon = R.drawable.mention,
                MentionTextColor,
                user,
                globalClickEnabled = true,
                onClick = {
                    selectedUserIds.add(user.uid)
                    onValueChange("")
                }
            )
        }
    }
}

@Composable
private fun SelectedResult(
    selectedUserIds: SnapshotStateList<String>,
    userViewModel: FirebaseUserViewModel
) {
    LazyColumn {
        items(selectedUserIds) { userId ->
            val user by remember(userId) {
                userViewModel.getUserById(userId)
            }.collectAsState(initial = null)

            SearchUserItem(
                icon = R.drawable.close,
                Color.White,
                user,
                globalClickEnabled = false,
                onClick = { selectedUserIds.remove(userId) }
            )
        }
    }
}

@Composable
private fun SearchUserItem(
    @DrawableRes icon: Int,
    color: Color,
    user: UserEntityFirebase?,
    globalClickEnabled: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = globalClickEnabled) { onClick() }
            .padding(8.dp)
    ) {
        ItemAvatarAndUsername(user)
        ItemActionIcon(icon, color, globalClickEnabled, onClick)
    }
}

@Composable
private fun ItemActionIcon(
    icon: Int,
    color: Color,
    globalClickEnabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(SecondaryDark, CircleShape)
            .size(32.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = "Mention",
            tint = color,
            modifier = Modifier.size(if (!globalClickEnabled) 14.dp else 22.dp)
        )
    }
}

@Composable
private fun ItemAvatarAndUsername(user: UserEntityFirebase?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(
            model = user?.avatarUrl,
            contentDescription = "Avatar",
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .gradientCircleBorder(),
            contentScale = ContentScale.Crop
        )

        Text(
            text = user?.username ?: "",
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 250.dp)
        )
    }
}

@Composable
private fun SearchField(value: String, onValueChange: (String) -> Unit) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        cursorBrush = SolidColor(Color.White),
        textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.CenterStart) {
                innerTextField()

                if (value.isEmpty()) {
                    Text(text = "Search people...", fontSize = 14.sp, color = GrayTextColor)
                }
            }
        },
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .background(PrimaryDark, CircleShape)
            .padding(12.dp)
            .fillMaxWidth()
            .height(28.dp)
    )
}