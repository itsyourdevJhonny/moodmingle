package com.emc.moodmingle.ui.create.post.dialogs

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.ui.create.post.CreatePostDialogHeader
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.TagBackground
import com.emc.moodmingle.ui.theme.TagTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.rememberUsersByIds
import com.emc.moodmingle.utils.modifier.gradientCircleBorder
import com.emc.moodmingle.utils.text.NumberFormatter
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel

@Composable
fun CreatePostTagDialog(
    tagUserIds: List<String>,
    onTaggedUsers: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    val selectedUserIds = remember { mutableStateListOf<String>() }

    if (tagUserIds.isNotEmpty()) selectedUserIds.addAll(tagUserIds)

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            CreatePostDialogHeader(
                label = "Tag People",
                enabled = selectedUserIds.isNotEmpty(),
                onOkay = {
                    onTaggedUsers(selectedUserIds.toList())
                    onDismiss()
                },
                onBack = onDismiss
            )
        }
    ) { paddingValues ->
        TagDialogContent(paddingValues, selectedUserIds)
    }
}

@Composable
private fun TagDialogContent(
    paddingValues: PaddingValues,
    selectedUserIds: SnapshotStateList<String>
) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()

    var value by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(paddingValues)) {
        TaggedPeopleCounter(selectedUserIds)
        SearchField(value, onValueChange = { value = it })

        if (value.isNotBlank()) {
            SearchResultSection(
                value,
                selectedUserIds,
                userViewModel,
                onValueChange = { value = it }
            )
        } else {
            if (selectedUserIds.isNotEmpty()) {
                SelectedSection(selectedUserIds, userViewModel)
            } else {
                SuggestionSection(selectedUserIds, userViewModel)
            }
        }
    }
}

@Composable
fun SelectedSection(
    selectedUserIds: SnapshotStateList<String>,
    userViewModel: FirebaseUserViewModel
) {
    LazyColumn {
        items(selectedUserIds) { userId ->
            val user by remember(userId) {
                userViewModel.getUserById(userId)
            }.collectAsState(initial = null)

            user?.let {
                UserItem(
                    iconRes = R.drawable.close,
                    label = "Untagged",
                    textColor = Color.White,
                    backgroundColor = PrimaryDark,
                    user = it,
                    globalClickEnabled = false,
                    typeMaxWidth = 54.dp,
                    onClick = { selectedUserIds.remove(userId) }
                )
            }
        }
    }
}

@Composable
fun SearchResultSection(
    value: String,
    selectedUserIds: SnapshotStateList<String>,
    userViewModel: FirebaseUserViewModel,
    onValueChange: (String) -> Unit
) {
    val searchedUsers by remember(value) {
        userViewModel.searchByUsername(value)
    }.collectAsState(initial = emptyList())

    LazyColumn {
        items(searchedUsers.filterNot { it.uid in selectedUserIds }) { user ->
            UserItem(
                iconRes = R.drawable.tag_people,
                label = "Tag",
                textColor = TagTextColor,
                backgroundColor = TagBackground,
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
private fun SuggestionSection(
    selectedUserIds: SnapshotStateList<String>,
    userViewModel: FirebaseUserViewModel
) {
    val currentUser by userViewModel.loggedUser

    val allUsers by userViewModel.getAllUsers().collectAsState(initial = emptyList())

    val followers by rememberUsersByIds(currentUser?.followerIds, userViewModel)
    val followings by rememberUsersByIds(currentUser?.followingIds, userViewModel)

    val excludedIds = remember(followers, followings) {
        (followers + followings).map { it.uid }.toSet()
    }

    LazyColumn {
        suggestionSection("Followers", followers, selectedUserIds)

        item { HorizontalDivider(thickness = 0.5.dp) }

        suggestionSection("Following", followings, selectedUserIds)

        item { HorizontalDivider(thickness = 0.5.dp) }

        suggestionSection(
            "Other People",
            allUsers.filterNot { it.uid in excludedIds },
            selectedUserIds
        )
    }
}

@Composable
fun SuggestionLabel(label: String) {
    Text(
        text = label,
        style = Typography.bodyMedium.copy(color = GrayTextColor),
        modifier = Modifier.padding(start = 16.dp, top = 8.dp)
    )
}

private fun LazyListScope.suggestionSection(
    title: String,
    users: List<UserEntityFirebase>,
    selectedUserIds: SnapshotStateList<String>
) {
    item { SuggestionLabel(title) }

    if (users.isEmpty()) {
        item { CircularProgressIndicator(modifier = Modifier.padding(16.dp)) }
    } else {
        items(users) { user ->
            SuggestionUserItem(user, selectedUserIds)
        }
    }
}

@Composable
private fun SuggestionUserItem(
    user: UserEntityFirebase,
    selectedUserIds: SnapshotStateList<String>
) {
    UserItem(
        iconRes = R.drawable.tag_people,
        label = "Tag",
        textColor = TagTextColor,
        backgroundColor = TagBackground,
        user,
        globalClickEnabled = true,
        onClick = { selectedUserIds.add(user.uid) }
    )
}

@Composable
private fun UserItem(
    @DrawableRes iconRes: Int,
    label: String,
    textColor: Color,
    backgroundColor: Color,
    user: UserEntityFirebase,
    globalClickEnabled: Boolean,
    typeMaxWidth: Dp = Dp.Unspecified,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(globalClickEnabled) { onClick() }
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .gradientCircleBorder(),
                contentScale = ContentScale.Crop
            )

            Column {
                Text(
                    text = user.username,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 210.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf(
                        user.followerIds.size to "Followers",
                        user.followingIds.size to "Following",
                    ).forEach { (count, type) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = NumberFormatter.formatValue(count.toLong() + 9999999, false),
                                style = Typography.bodySmall.copy(color = Color.White)
                            )

                            Text(
                                text = type,
                                style = Typography.bodySmall.copy(color = GrayTextColor),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = typeMaxWidth)
                            )
                        }
                    }
                }
            }
        }

        TextButton(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = textColor
            )
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = label,
                modifier = Modifier.size(if (label == "Untagged") 16.dp else 20.dp)
            )

            Text(text = " $label")
        }
    }
}


@Composable
private fun TaggedPeopleCounter(selectedUserIds: SnapshotStateList<String>) {
    AnimatedVisibility(visible = selectedUserIds.isNotEmpty()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
        ) {
            Text(
                text = "${selectedUserIds.size}",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Text(text = " people to be tagged", color = GrayTextColor)
        }
    }
}

@Composable
private fun SearchField(value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = "Search people to tag...") },
        shape = CircleShape,
        trailingIcon = {
            if (value.isNotBlank()) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier.clickable { onValueChange("") }
                )
            }
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = PrimaryDark,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedTextColor = Color.White,
            unfocusedPlaceholderColor = GrayTextColor,
            focusedTextColor = Color.White,
            focusedContainerColor = PrimaryDark,
            focusedIndicatorColor = Color.Transparent,
            cursorColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )
}