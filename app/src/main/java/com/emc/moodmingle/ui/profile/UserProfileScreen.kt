package com.emc.moodmingle.ui.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.data.model.post.user.CombinedPost
import com.emc.moodmingle.di.AppDatabase
import com.emc.moodmingle.ui.screens.LoadingMorePosts
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.VerifiedColor
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import com.emc.moodmingle.viewmodel.firebase.PostViewModelFirebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull

@SuppressLint("MutableCollectionMutableState")
@Composable
fun UserProfileScreen(onBackClick: () -> Unit, userUid: String) {
    val context = LocalContext.current

    val userDao = remember { AppDatabase.getDatabase(context).userDao() }
    var currentUserUid by remember { mutableStateOf("") }

    val userViewModel: FirebaseUserViewModel = hiltViewModel()
    val postViewModel: PostViewModelFirebase = hiltViewModel()
    val user by userViewModel.getUserByUid(userUid).collectAsState(initial = null)
    var combinedPosts by remember { mutableStateOf(emptyList<CombinedPost>()) }

    LaunchedEffect(combinedPosts) {
        combinedPosts = postViewModel.getCombinedPostsByUser(userUid)
    }

    var displayedPosts by remember { mutableStateOf(emptyList<CombinedPost>()) }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        currentUserUid = userDao.getLoggedUser()?.uid ?: ""
    }

    LaunchedEffect(combinedPosts) {
        if (combinedPosts.isNotEmpty()) displayedPosts = combinedPosts.take(2)
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .filterNotNull()
            .distinctUntilChanged()
            .collect { lastVisible ->
                if (combinedPosts.isEmpty()) return@collect
                val nearEnd = displayedPosts.lastIndex - 1

                if (lastVisible >= nearEnd && displayedPosts.size < combinedPosts.size) {
                    delay(200)
                    displayedPosts = combinedPosts.take(displayedPosts.size + 3)
                }
            }
    }

    val userMap by remember {
        mutableStateOf(mutableMapOf<String, UserEntityFirebase?>())
    }

    LaunchedEffect(displayedPosts) {
        displayedPosts.forEach { post ->
            if (!userMap.containsKey(post.postEntity?.userId)) {
                userViewModel.getUserByUid(post.postEntity?.userId ?: "")
                    .collect { user ->
                        userMap[post.postEntity?.userId ?: ""] = user.getOrNull()
                    }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 26.dp)
            .background(MaterialTheme.colorScheme.surface),
    ) {
        Header(onBackClick, user?.getOrNull()?.username ?: "", userUid)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            horizontalAlignment = Alignment.CenterHorizontally,
            state = listState
        ) {
            item { UserProfileAvatar(user?.getOrNull()) }
            item { UserProfileUsername(username = user?.getOrNull()?.username ?: "") }
            item {
                TotalPost(
                    isCurrentUser = currentUserUid == userUid,
                    postCount = combinedPosts.size
                )
            }
            item { DrawUserProfileLine() }
            item { CreateBio(user?.getOrNull()?.bio ?: "") }
            item { Spacer(Modifier.height(16.dp)) }
            item { CreateJoinedDate(user?.getOrNull()?.joinedDate ?: "") }
            item { DrawUserProfileLine() }
            item {
                UserProfileMoreAction(
                    isCurrentUser = currentUserUid == userUid,
                    postCount = combinedPosts.size
                )
            }

            if (combinedPosts.isEmpty()) {
                item {
                    Text(
                        text = "No post yet.",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (displayedPosts.isNotEmpty() && displayedPosts[0].shareEntity != null) {
                item { DrawUserNoPaddingLine() }
            }

            itemsIndexed(
                items = displayedPosts,
                key = { index, post -> "${post.type.name}_${post.id}" }
            ) { _, post ->
//                UserPostContent(post, postViewModel)
                Spacer(Modifier.height(2.dp))
            }

            if (combinedPosts.isNotEmpty()) {
                item { LoadingMorePosts() }
            }
        }
    }
}

@Composable
fun UserProfileAvatar(user: UserEntityFirebase?) {
    if (user != null) {
        Avatar(avatarUrl = user.avatarUrl)
    } else {
        Text(
            text = "Loading...",
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
        )
    }
}

@Composable
fun UserProfileUsername(username: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "@$username",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White
            ),
            modifier = Modifier
                .padding(top = 12.dp, bottom = 8.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawGradient()
        )

        Icon(
            modifier = Modifier.size(22.dp),
            painter = painterResource(R.drawable.verified),
            contentDescription = "Verified",
            tint = VerifiedColor
        )
    }
}

@Composable
fun TotalPost(isCurrentUser: Boolean, postCount: Int) {
    val postLabel = if (postCount == 1) "post" else "posts"
    val prefix = if (isCurrentUser) "You have" else ""

    Text(
        text = "$prefix $postCount $postLabel",
        style = MaterialTheme.typography.titleSmall.copy(color = Color.White)
    )
}

@Composable
fun UserProfileMoreAction(isCurrentUser: Boolean, postCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val postLabel = if (postCount == 1) "post" else "posts"

        val textLabel = if (isCurrentUser) {
            "Your $postLabel"
        } else {
            postLabel.replaceFirstChar { it.uppercase() }
        }

        Text(
            text = textLabel,
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start
            )
        )

        Icon(
            painter = painterResource(id = R.drawable.more),
            contentDescription = "More",
            tint = Color.White
        )
    }
}

/*@Composable
fun DisplayPostContent(post: CombinedPost, postViewModel: PostViewModelFirebase) {
    if (post.type == com.emc.moodmingle.data.model.post.user.PostType.SHARED_POST) {
        val shareEntity = post.shareEntity
        val postId = shareEntity!!.postId
        val context = LocalContext.current

        var postEntity by remember { mutableStateOf<PostEntityFirebase?>(null) }

        LaunchedEffect(postId) {
            postEntity = try {
                postViewModel.getPostByIdOnce(postId)
            } catch (_: Exception) {
                Toast.makeText(context, "Failed to load post", Toast.LENGTH_SHORT).show()
                null
            }
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SecondaryDark),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                postEntity?.let { SharedUserInformation(shareEntity) }
            }

            if (postEntity == null) {
                SharedPostSkeleton()
            } else {
                RenderMultimediaContent(postEntity!!)
            }
        }
    } else {
        post.postEntity?.let { RenderMultimediaContent(it) }
    }
}

*//**
 * Renders multimedia content (text, image, video, audio) based on the post type.
 *//*
@Composable
fun RenderMultimediaContent(postEntity: PostEntityFirebase) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    var userEntity by remember { mutableStateOf<UserEntityFirebase?>(null) }

    LaunchedEffect(postEntity.userId) {
        userEntity = userViewModel.getUserCached(postEntity.userId)
    }

    var showShareSheet by remember { mutableStateOf(false) }

    if (postEntity.urls.isNotEmpty()) {
        MultimediaCard(
            composable = {
                PostMedia(
                    mediaUrls = postEntity.urls,
                    onShowShareSheet = { showShareSheet = it }
                )
            },
            postEntity = postEntity,
            userEntity = userEntity,
            postType = "IMAGE",
            onClick = {},
            showShareSheet = showShareSheet,
            onShowShareSheet = { showShareSheet = it },
            onChatClick = { _, _ -> }
        )
    } else {
        MultimediaCard(
            composable = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    content = {
                        ExpandableAutoDetectClickableText(
                            fullText = postEntity.description,
                            style = MaterialTheme.typography.bodyLarge,
                            hasPadding = false,
                        )
                    }
                )
            },
            postEntity = postEntity,
            userEntity = userEntity,
            postType = "TEXT",
            onClick = {},
            showShareSheet = showShareSheet,
            onShowShareSheet = { showShareSheet = it },
            onChatClick = { _, _ -> }
        )
    }
}

@Composable
fun SharedUserInformation(shareEntity: ShareEntityFirebase) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val shareViewModel = hiltViewModel<ShareViewModelFirebase>()

    val userEntity by remember(shareEntity.userUid) { userViewModel.getUserByUid(shareEntity.userUid) }
        .collectAsState(initial = null)

    val shareEntity by remember(shareEntity.postId) { shareViewModel.getSharedByPostId(shareEntity.postId) }
        .collectAsState(initial = null)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
//            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            userEntity?.let { userResult ->
                val user = userResult
                AvatarImage(user.getOrNull()?.avatarUrl ?: "", {}, user.getOrNull()?.uid ?: "")

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = formatUsername(user.getOrNull()?.username ?: ""),
                        style = Typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 185.dp)
                    )

                    shareEntity?.let { share ->
                        Text(
                            text = formatTimeAgo(share.time),
                            fontSize = 12.sp,
                            color = GrayTextColor
                        )
                    }
                }
            }
        }

        Box(modifier = Modifier.background(TertiaryDark, RoundedCornerShape(8.dp))) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.share),
                    contentDescription = "Check",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )

                Text(
                    text = "Shared",
                    style = Typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}*/

@Composable
fun Header(onBackClick: () -> Unit, username: String, userUid: String) {
    val context = LocalContext.current
    val userDao = remember { AppDatabase.getDatabase(context).userDao() }
    var currentUserUid by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        currentUserUid = userDao.getLoggedUser()?.uid ?: ""
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                modifier = Modifier.size(28.dp),
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Text(
            text = if (currentUserUid == userUid) "Your Profile" else username,
            style = MaterialTheme.typography.titleSmall.copy(color = Color.White)
        )
    }
}

@Composable
fun Avatar(avatarUrl: String) {
    AsyncImage(
        model = avatarUrl,
        contentDescription = "Avatar",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.3f))
    )
}

@Composable
fun DrawUserProfileLine() {
    DrawUserNoPaddingLine(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
}

@Composable
fun DrawUserNoPaddingLine(modifier: Modifier = Modifier, thickness: Dp = 1.dp) {
    HorizontalDivider(
        modifier = modifier
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    drawRect(
                        brush = BrushPrimaryGradient,
                        blendMode = BlendMode.SrcAtop
                    )
                }
            },
        thickness = thickness
    )
}