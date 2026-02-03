package com.emc.moodmingle.ui.create.post.settings.audience

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.ui.create.post.CreatePostDialogHeader
import com.emc.moodmingle.ui.theme.PurplePrimary
import com.emc.moodmingle.utils.components.rememberUsersByIds
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.utils.modifier.gradientCircleBorder
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.collections.isNotEmpty
import kotlin.collections.map

@Composable
fun CustomAudienceDialog(
    headerLabel: String,
    audience: Any,
    onAudienceSelected: (Any) -> Unit,
    onDismiss: () -> Unit
) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()

    val currentUser by userViewModel.loggedUser

    var searchedUsers by remember { mutableStateOf<List<UserEntityFirebase>>(emptyList()) }
    var search by remember { mutableStateOf("") }

    val selectedUserIds = remember { mutableStateListOf<String>() }

    LaunchedEffect(audience) {
        if ((audience is SnapshotStateList<*> || audience is List<*>) && audience.isNotEmpty()) {
            selectedUserIds.addAll(audience.map { it.toString() })
        }
    }

    val allUsers by userViewModel.getAllUsers().collectAsState(initial = emptyList())
    val followers by rememberUsersByIds(currentUser?.followerIds, userViewModel)
    val followings by rememberUsersByIds(currentUser?.followingIds, userViewModel)
    val supporters by rememberUsersByIds(currentUser?.supporterIds, userViewModel)
    val selectedUsers by rememberUsersByIds(selectedUserIds.toList(), userViewModel)

    val combinedUsers = (selectedUsers + followers + followings + supporters).distinct()
    val excludedIds = remember(combinedUsers) { combinedUsers.map { it.uid }.toSet() }

    val filteredAllUsers = allUsers.filterNot { it.uid in excludedIds }
    val filteredSelectedUsers = selectedUsers.filterNot { it.uid in excludedIds }
    val filteredSearchedUsers = searchedUsers.filterNot { it.uid in selectedUserIds }

    LaunchedEffect(search) {
        if (search.isNotBlank()) searchedUsers = userViewModel.searchByUsername(search).first()
    }

    val displayedUsers = if (search.isNotBlank()) filteredSearchedUsers
    else filteredSelectedUsers + combinedUsers + filteredAllUsers

    val sortedDisplayedUsers = remember(displayedUsers) {
        displayedUsers.sortedWith(compareByDescending<UserEntityFirebase> {
            selectedUserIds.contains(it.uid)
        }.thenBy { it.username.lowercase() })
    }

    BackHandler { onDismiss() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(top = 8.dp, bottom = 42.dp)
    ) {
        CreatePostDialogHeader(
            label = headerLabel,
            enabled = selectedUserIds.isNotEmpty(),
            onOkay = { onAudienceSelected(selectedUserIds); onDismiss() },
            onBack = { onDismiss() }
        )

        SearchField(search) { search = it }

        HorizontalDivider(thickness = 0.5.dp)

        Spacer(Modifier.height(16.dp))

        if (displayedUsers.isEmpty()) {
            CircularProgressIndicator(
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .drawGradient()
            )
        }

        LazyColumn(state = listState) {
            items(items = sortedDisplayedUsers, key = { it.uid }) { user ->
                SearchedUserItem(
                    search,
                    user,
                    selectedUserIds,
                    scope,
                    keyboardController,
                    listState,
                    onSearchChanged = { search = it }
                )
            }
        }
    }
}

@Composable
private fun SearchField(search: String, onSearchChanged: (String) -> Unit) {
    OutlinedTextField(
        value = search,
        onValueChange = onSearchChanged,
        placeholder = { Text(text = "Search people...", color = Color.Gray) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    )
}

@Composable
private fun LazyItemScope.SearchedUserItem(
    search: String,
    user: UserEntityFirebase,
    selectedUserIds: SnapshotStateList<String>,
    scope: CoroutineScope,
    keyboardController: SoftwareKeyboardController?,
    listState: LazyListState,
    onSearchChanged: (String) -> Unit
) {
    val userId = user.uid
    val isChecked = selectedUserIds.contains(userId)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (search.isNotBlank()) onSearchChanged(""); keyboardController?.hide()
                if (selectedUserIds.contains(userId)) selectedUserIds.remove(userId)
                else selectedUserIds.add(userId)
                scope.launch { listState.animateScrollToItem(0) }
            }
            .padding(8.dp)
            .animateItem()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ItemUserAvatar(user)
            ItemUsername(user)
        }

        ItemCheckBox(isChecked)
    }
}

@Composable
private fun ItemCheckBox(isChecked: Boolean) {
    Checkbox(
        checked = isChecked,
        onCheckedChange = null,
        colors = CheckboxDefaults.colors(
            checkedColor = PurplePrimary,
            checkmarkColor = Color.White,
            uncheckedColor = Color.White
        )
    )
}

@Composable
private fun ItemUsername(user: UserEntityFirebase) {
    Text(
        text = user.username,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.widthIn(max = 250.dp)
    )
}

@Composable
private fun ItemUserAvatar(user: UserEntityFirebase) {
    AsyncImage(
        model = user.avatarUrl,
        contentDescription = "Avatar",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .gradientCircleBorder()
    )
}