package com.emc.moodmingle.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import coil.request.ImageRequest
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.PostEntityFirebase
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.data.model.post.user.CombinedPost
import com.emc.moodmingle.ui.profile.ProfileSection
import com.emc.moodmingle.ui.profile.UserPostContent
import com.emc.moodmingle.ui.profile.tab.FilterUserPosts
import com.emc.moodmingle.ui.profile.tab.ProfileTabs
import com.emc.moodmingle.ui.settings.saved.utils.EmptyComponent
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.utils.text.TextFormatter
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import com.emc.moodmingle.viewmodel.firebase.PostViewModelFirebase
import com.emc.moodmingle.viewmodel.firebase.ShareViewModelFirebase
import com.emc.moodmingle.viewmodel.firebase.favorites.FavoritesViewModelFirebase
import com.emc.moodmingle.viewmodel.firebase.saved.SaveViewModelFirebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@SuppressLint("MutableCollectionMutableState")
@Composable
fun ProfileScreen(
    isFromOtherUser: Boolean = false,
    otherUserId: String = "",
    onChatClick: (String, String) -> Unit,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()

    val loggedUser by userViewModel.loggedUser

    val userUid = if (isFromOtherUser) otherUserId else loggedUser?.uid ?: ""

    val postViewModel = hiltViewModel<PostViewModelFirebase>()
    val shareViewModel = hiltViewModel<ShareViewModelFirebase>()
    val saveViewModel = hiltViewModel<SaveViewModelFirebase>()
    val favoritesViewModel = hiltViewModel<FavoritesViewModelFirebase>()

    var user by remember { mutableStateOf<UserEntityFirebase?>(null) }

    LaunchedEffect(userUid) {
        if (!isFromOtherUser) {
            user = userViewModel.loggedUser.value
        } else {
            userViewModel.getUserByUid(userUid).collect {
                user = it.getOrNull()
            }
        }
    }

    var posts by remember { mutableStateOf(emptyList<PostEntityFirebase>()) }

    LaunchedEffect(userUid) {
        posts = postViewModel.getPostsByUserId(userUid)
    }

    val shares by remember(userUid) {
        shareViewModel.getSharedByUserUid(userUid)
    }.collectAsState(initial = emptyList())

    val saves by remember(userUid) {
        saveViewModel.getSavedByUser(userUid)
    }.collectAsState(initial = emptyList())

    val favorites by remember(userUid) {
        favoritesViewModel.getFavoritesByUser(userUid)
    }.collectAsState(initial = emptyList())

    val combinedPosts by remember(userUid) {
        postViewModel.getCombinedPostsByUserFlow(userUid)
    }.collectAsState(initial = emptyList())

    var displayedPosts by rememberSaveable { mutableStateOf(emptyList<CombinedPost>()) }
    displayedPosts = combinedPosts

    var selectedTab by rememberSaveable { mutableStateOf("All") }

    LaunchedEffect(displayedPosts) {
        val imageLoader = ImageLoader(context)

        val newPosts = displayedPosts.takeLast(2)

        withContext(Dispatchers.IO) {
            newPosts.forEach { post ->
                if (post.postEntity?.type == "IMAGE") {
                    val request = ImageRequest.Builder(context)
                        .data(post.postEntity.urls)
                        .build()
                    imageLoader.enqueue(request)
                }
            }
        }
    }

    val scrollState = rememberScrollState()

    var pageSize by rememberSaveable { mutableIntStateOf(10) }
    var isLoadingMore by rememberSaveable { mutableStateOf(false) }

    val pagedPosts by remember(combinedPosts, pageSize) {
        mutableStateOf(combinedPosts.take(pageSize))
    }

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.value to scrollState.maxValue }
            .collect { (value, maxValue) ->
                val threshold = 200
                val isNearBottom = maxValue > 0 && value >= maxValue - threshold

                if (isNearBottom && !isLoadingMore && pageSize < combinedPosts.size) {
                    isLoadingMore = true
                    delay(300)
                    pageSize += 10
                    isLoadingMore = false
                }
            }
    }

    Column(
        modifier = Modifier
            .padding(
                top = if (isFromOtherUser) 32.dp else 0.dp,
                bottom = if (isFromOtherUser) 42.dp else 0.dp
            )
    ) {
        if (isFromOtherUser) {
            user?.let {
                Header(it, onBack)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .verticalScroll(scrollState)
        ) {
            ProfileSection(
                isFromOtherUser = isFromOtherUser,
                user = user,
                postCount = posts.size.toLong(),
                shareCount = shares.size.toLong(),
                saveCount = saves.size.toLong(),
                favoritesCount = favorites.size.toLong()
            )

            ProfileTabs(onSelectedTab = { selectedTab = it })

            when (selectedTab) {
                "Text" -> FilterUserPosts("TEXT", pagedPosts, onChatClick)
                "Media" -> FilterUserPosts("MEDIA", pagedPosts, onChatClick)
                "Reposts" -> {}
                else -> {
                    if (pagedPosts.isEmpty()) {
                        EmptyComponent(R.drawable.empty, "No posts.")
                    } else {
                        pagedPosts.forEach { post ->
                            UserPostContent(post, postViewModel, onChatClick)
                        }
                    }
                }
            }

            if (isLoadingMore) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun Header(user: UserEntityFirebase, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(PrimaryDark)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier.clickable { onBack() },
            tint = Color.White
        )

        Text(
            text = TextFormatter.formatTextWithSuffixS(user.username) + " Profile",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                textAlign = TextAlign.Center
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}