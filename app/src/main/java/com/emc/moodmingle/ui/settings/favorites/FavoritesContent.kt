package com.emc.moodmingle.ui.settings.favorites

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.post.normal.PostEntityFirebase
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.domain.remote.model.favorites.FavoritesCollectionEntity
import com.emc.moodmingle.domain.remote.model.favorites.FavoritesEntityFirebase
import com.emc.moodmingle.domain.local.model.post.formatTimeAgo
import com.emc.moodmingle.ui.post.action.DrawNoPaddingLine
import com.emc.moodmingle.ui.screens.NewCollectionButton
import com.emc.moodmingle.ui.settings.saved.BottomSheetItem
import com.emc.moodmingle.ui.settings.saved.media.isAudio
import com.emc.moodmingle.ui.settings.saved.media.isImage
import com.emc.moodmingle.ui.settings.saved.media.isVideo
import com.emc.moodmingle.ui.settings.saved.utils.EmptyComponent
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.HeartColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.LoadingDialog
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.remote.CommentViewModelFirebase
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel
import com.emc.moodmingle.viewmodel.remote.PostViewModelFirebase
import com.emc.moodmingle.viewmodel.remote.ReactionViewModelFirebase
import com.emc.moodmingle.viewmodel.remote.ShareViewModelFirebase
import com.emc.moodmingle.viewmodel.remote.favorites.FavoritesCollectionViewModel
import com.emc.moodmingle.viewmodel.remote.favorites.FavoritesViewModelFirebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Composable
fun FavoritesContent(
    favorites: List<FavoritesEntityFirebase>?,
    isLoading: Boolean,
    userId: String
) {
    when {
        isLoading -> {
            LoadingContent()
            return
        }

        favorites != null && favorites.isEmpty() -> {
            EmptyComponent(R.drawable.empty, "No favorites yet.")
            return
        }

        favorites != null -> {
            PaginatedFavoritesGrid(favorites, userId)
        }
    }
}

@Composable
fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(Modifier.height(12.dp))
            Text(text = "Loading favorites...", style = Typography.bodyMedium)
        }
    }
}

@Composable
fun PaginatedFavoritesGrid(filteredFavorites: List<FavoritesEntityFirebase>, userId: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userViewModelFirebase = hiltViewModel<FirebaseUserViewModel>()
    val favoritesViewModelFirebase = hiltViewModel<FavoritesViewModelFirebase>()
    val postViewModelFirebase = hiltViewModel<PostViewModelFirebase>()
    val collectionViewModelFirebase = hiltViewModel<FavoritesCollectionViewModel>()

    var showSheet by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var type by remember { mutableStateOf("") }
    var selectedFavorite by remember { mutableStateOf<FavoritesEntityFirebase?>(null) }
    var selectedCollectionName by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    val listState = rememberLazyGridState()
    val userCache = remember { mutableStateMapOf<String, UserEntityFirebase?>() }
    val postCache = remember { mutableStateMapOf<String, PostEntityFirebase?>() }
    val pageSize = 8

    val sortedFavorites = remember(filteredFavorites) {
        filteredFavorites.sortedWith(
            compareByDescending<FavoritesEntityFirebase> { it.pinned }
                .thenByDescending { it.time }
        )
    }

    // make loadedCount reactive to saved.size
    var loadedCount by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(sortedFavorites) {
        loadedCount = sortedFavorites.size.coerceAtMost(pageSize)
    }

    // visible list derived from loadedCount
    val visibleList by remember(loadedCount, sortedFavorites) {
        derivedStateOf { sortedFavorites.take(loadedCount) }
    }

    LaunchedEffect(visibleList) {
        visibleList.forEach { save ->
            if (!userCache.containsKey(save.userUid)) {
                val user = userViewModelFirebase.getUserByUid(save.userUid)
                    .first()
                    .getOrNull()
                userCache[save.userUid] = user
            }
            if (!postCache.containsKey(save.postId)) {
                val post = postViewModelFirebase.getPostById(save.postId)
                    .first()
                postCache[save.postId] = post
            }
        }
    }

    // -----------------------
    // PAGINATION TRIGGER ON SCROLL
    // -----------------------
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .filterNotNull()
            .collect { lastVisible ->
                val nearEnd = visibleList.lastIndex - 1
                if (lastVisible >= nearEnd && visibleList.size < sortedFavorites.size) {
                    // load next batch
                    loadedCount = (visibleList.size + pageSize).coerceAtMost(sortedFavorites.size)
                }
            }
    }

    // flag to track loading
    val firstPage = sortedFavorites.take(loadedCount)
    var firstPageReady by remember { mutableStateOf(false) }

    LaunchedEffect(firstPage) {
        firstPage.forEach { save ->
            if (!userCache.containsKey(save.userUid)) {
                val user = userViewModelFirebase.getUserByUid(save.userUid).first().getOrNull()
                userCache[save.userUid] = user
            }
            if (!postCache.containsKey(save.postId)) {
                val post = postViewModelFirebase.getPostById(save.postId).first()
                postCache[save.postId] = post
            }
        }
        firstPageReady = true
    }

    // -----------------------
    // 2. SHOW LOADING UNTIL FIRST PAGE READY
    // -----------------------
    if (!firstPageReady) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(visibleList, key = { it.postId }) { favorite ->

            if (userCache[favorite.userUid] == null && postCache[favorite.postId] == null) {
                FavoriteSkeletonCard()
            } else {
                FavoriteItemCard(
                    userCache,
                    postCache,
                    filteredFavorite = favorite,
                    onShowSheet = { isShow ->
                        selectedFavorite = favorite
                        showSheet = isShow
                    }
                )
            }
        }
    }

    if (showSheet) {
        ShowBottomSheet(
            favorite = selectedFavorite!!,
            postViewModelFirebase = postViewModelFirebase,
            onTypeChange = { type = it },
            onLoading = { isLoading = it },
            onShowSheet = { showSheet = it },
            onSelectedFavorite = { selectedFavorite = it },
            onSelectedCollectionName = { selectedCollectionName = it },
            onShowDialog = { showDialog = it }
        )
    }

    if (isLoading) {
        Log.d("FAVORITES", "TYPE: $type")
        LoadingDialog(
            text = when (type) {
                "Pin", "Unpin" -> if (type == "Pin") "Pinning" else "Unpinning"
                "Add To Collection", "Remove From Collection $selectedCollectionName" -> {
                    "${if (type == "Add To Collection") "Adding to" else "Removing from"} collection"
                }

                "Remove" -> "Removing"
                else -> ""
            }
        ) {
            scope.launch {
                when (type) {
                    "Pin", "Unpin" -> favoritesViewModelFirebase.update(
                        favoritesEntity = selectedFavorite!!.copy(
                            pinned = !selectedFavorite!!.pinned
                        )
                    )

                    "Remove" -> favoritesViewModelFirebase.delete(selectedFavorite!!)
                    "Add To Collection", "Remove From Collection $selectedCollectionName" -> {
                        val isRemove = type == "Remove From Collection $selectedCollectionName"
                        var collection: FavoritesCollectionEntity?

                        if (isRemove) {
                            collection =
                                collectionViewModelFirebase.getCollectionByUserAndFavorite(
                                    userUid = userId,
                                    favoriteId = selectedFavorite!!.id
                                )

                            collectionViewModelFirebase.update(
                                collection = collection!!.copy(favoritesIds = collection.favoritesIds - selectedFavorite!!.id)
                            )
                        } else {
                            collection =
                                collectionViewModelFirebase.getCollectionByNameAndUser(name, userId)

                            collectionViewModelFirebase.update(
                                collection = collection!!.copy(favoritesIds = collection.favoritesIds + selectedFavorite!!.id)
                            )
                        }

                        delay(1000)

                        Toast.makeText(
                            context,
                            "Item ${if (isRemove) "removed" else "added"} to collection $name",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                isLoading = false
            }
        }
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            val collections by remember(userId) {
                collectionViewModelFirebase.getCollectionByUser(userId)
                    .stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())
            }.collectAsState(emptyList())

            val collectionNames = collections.map { it.name }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
                    .background(PrimaryDark)
                    .border(
                        width = 0.5.dp,
                        brush = BrushPrimaryGradient,
                        shape = RectangleShape
                    )
                    .padding(vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Select Collection",
                        style = Typography.titleLarge
                    )

                    NewCollectionButton(userId)

                    DrawNoPaddingLine(
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    if (collectionNames.isEmpty()) {
                        EmptyComponent(
                            R.drawable.empty,
                            text = "No collections yet. Create one."
                        )
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            items(collectionNames) { collectionName ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            isLoading = true
                                            showDialog = false
                                            name = collectionName
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = collectionName,
                                            overflow = TextOverflow.Ellipsis,
                                            maxLines = 1,
                                            modifier = Modifier.widthIn(max = 270.dp)
                                        )

                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add",
                                            modifier = Modifier
                                                .graphicsLayer(alpha = 0.99f)
                                                .drawGradient()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun FavoriteItemCard(
    userCache: SnapshotStateMap<String, UserEntityFirebase?>,
    postCache: SnapshotStateMap<String, PostEntityFirebase?>,
    filteredFavorite: FavoritesEntityFirebase,
    onShowSheet: (Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    val postViewModelFirebase = hiltViewModel<PostViewModelFirebase>()
    val userViewModelFirebase = hiltViewModel<FirebaseUserViewModel>()
    val reactionViewModelFirebase = hiltViewModel<ReactionViewModelFirebase>()
    val commentViewModelFirebase = hiltViewModel<CommentViewModelFirebase>()
    val shareViewModelFirebase = hiltViewModel<ShareViewModelFirebase>()

    // --- ENTRANCE ANIMATION ---
    val enterAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        enterAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing)
        )
    }

    // USER STATE
    val userState: State<UserEntityFirebase?> =
        produceState(initialValue = null, filteredFavorite.userUid) {
            // try cache first
            value =
                userCache[filteredFavorite.userUid] ?: userViewModelFirebase.getUserByUid(
                    filteredFavorite.userUid
                )
                    .first()
                    .getOrNull()
                    .also { userCache[filteredFavorite.userUid] = it }
        }

    // POST STATE
    val postState: State<PostEntityFirebase?> =
        produceState(initialValue = null, filteredFavorite.postId) {
            value = postCache[filteredFavorite.postId] ?: postViewModelFirebase.getPostById(
                filteredFavorite.postId
            )
                .first()
                .also { postCache[filteredFavorite.postId] = it }
        }

    val user = userState.value
    val post = postState.value

    // --- PIN BOUNCE ANIMATION ---
    val scaleAnim = remember { Animatable(1f) }
    LaunchedEffect(filteredFavorite.pinned) {
        if (filteredFavorite.pinned) {
            scaleAnim.animateTo(
                targetValue = 1.1f,
                animationSpec = spring(
                    stiffness = Spring.StiffnessLow,
                    dampingRatio = Spring.DampingRatioMediumBouncy
                )
            )
            scaleAnim.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    stiffness = Spring.StiffnessMedium,
                    dampingRatio = Spring.DampingRatioNoBouncy
                )
            )
        }
    }

    // --- WIGGLE ROTATION ANIMATION ---
    val wiggleAnim = remember { Animatable(0f) }
    LaunchedEffect(filteredFavorite.pinned) {
        if (filteredFavorite.pinned) {
            wiggleAnim.animateTo(5f, tween(100))
            wiggleAnim.animateTo(-5f, tween(100))
            wiggleAnim.animateTo(0f, tween(100))
        }
    }

    if (user != null && post != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .height(164.dp)
                .graphicsLayer {
                    alpha = enterAnim.value
                    translationY = (50f * (1f - enterAnim.value))
                    scaleX = scaleAnim.value
                    scaleY = scaleAnim.value
                    rotationZ = wiggleAnim.value
                }
                .background(SecondaryDark, RoundedCornerShape(8.dp))
                .clickable {}
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                UserInformation(user, post, filteredFavorite, onShowSheet)

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    PostInformation(post)
                    MediaInformation(post)
                    InteractionInformation(
                        post,
                        scope,
                        reactionViewModelFirebase,
                        commentViewModelFirebase,
                        shareViewModelFirebase
                    )
                }

                Text(
                    text = "Added ${formatTimeAgo(filteredFavorite.time)}",
                    style = Typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun UserInformation(
    user: UserEntityFirebase,
    post: PostEntityFirebase,
    favorite: FavoritesEntityFirebase,
    onShowSheet: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = user.username,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Column {
                    Text(
                        text = formatTimeAgo(post.timeAgo),
                        style = Typography.labelSmall.copy(color = GrayTextColor)
                    )
                    Text(
                        text = user.username,
                        fontSize = 13.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }

            if (favorite.pinned) {
                Icon(
                    painter = painterResource(R.drawable.pin),
                    contentDescription = "Pinned",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Red
                )
            }

            Icon(
                painter = painterResource(R.drawable.more),
                contentDescription = "More",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(24.dp)
                    .clickable { onShowSheet(true) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShowBottomSheet(
    favorite: FavoritesEntityFirebase,
    postViewModelFirebase: PostViewModelFirebase,
    onTypeChange: (String) -> Unit,
    onLoading: (Boolean) -> Unit,
    onShowSheet: (Boolean) -> Unit,
    onSelectedFavorite: (FavoritesEntityFirebase) -> Unit,
    onSelectedCollectionName: (String) -> Unit,
    onShowDialog: (Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    val collectionViewModelFirebase = hiltViewModel<FavoritesCollectionViewModel>()

    val post by remember(favorite.postId) {
        postViewModelFirebase.getPostById(favorite.postId)
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), null)
    }.collectAsState(initial = null)

    val userId = post?.userId ?: ""

    ModalBottomSheet(
        onDismissRequest = { onShowSheet(false) },
        containerColor = PrimaryDark,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                modifier = Modifier
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient()
            )
        }
    ) {
        Column(
            modifier = Modifier
                .height(200.dp)
                .fillMaxSize()
                .background(PrimaryDark)
        ) {
            DrawNoPaddingLine(
                thickness = 0.5.dp,
                modifier = Modifier.padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
            )

            BottomSheetItem(
                text = if (favorite.pinned) "Unpin" else "Pin",
                iconRes = if (favorite.pinned) R.drawable.unpin else R.drawable.pin,
                onType = {
                    onSelectedFavorite(favorite)
                    onTypeChange(it)
                },
                onShowSheet = onShowSheet,
                onLoading = onLoading
            )

            var isSavedInCollection by remember { mutableStateOf(false) }

            LaunchedEffect(favorite.id, userId) {
                isSavedInCollection =
                    collectionViewModelFirebase.isSaveInCollection(
                        favorite.id,
                        userId
                    )
            }

            BottomSheetItem(
                text = if (isSavedInCollection) {
                    var collectionName by remember { mutableStateOf("") }

                    LaunchedEffect(userId, favorite.id) {
                        val collection =
                            collectionViewModelFirebase.getCollectionByUserAndFavorite(
                                userUid = userId,
                                favoriteId = favorite.id
                            )

                        collectionName = collection?.name ?: ""
                        onSelectedCollectionName(collectionName)
                    }

                    "Remove From Collection $collectionName"
                } else "Add To Collection",
                iconRes = R.drawable.collections,
                onType = onTypeChange,
                onShowSheet = onShowSheet,
                onLoading = { if (isSavedInCollection) onLoading(it) else onShowDialog(it) }
            )

            BottomSheetItem(
                text = "Remove",
                iconRes = R.drawable.remove,
                onType = {
                    onSelectedFavorite(favorite)
                    onTypeChange(it)
                },
                onShowSheet = onShowSheet,
                onLoading = onLoading
            )
        }
    }
}

@Composable
fun PostInformation(post: PostEntityFirebase) {
    val informationTypes = listOf(
        R.drawable.hashtag to post.hashtag,
        R.drawable.caption to post.caption,
        R.drawable.description to post.description
    )

    Column {
        informationTypes.forEach { informationType ->
            Information(informationType.first, informationType.second)
        }
    }
}

@Composable
private fun Information(@DrawableRes iconRes: Int, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = "Information",
            modifier = Modifier
                .size(16.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawGradient()
        )

        Text(
            text = text,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = Typography.bodySmall.copy(color = GrayTextColor)
        )
    }
}

@Composable
private fun MediaInformation(post: PostEntityFirebase) {
    val images = post.urls.filter { isImage(it) }
    val videos = post.urls.filter { isVideo(it) }
    val audios = post.urls.filter { isAudio(it) }

    val mediaTypes = listOf(
        R.drawable.image to images.size,
        R.drawable.video to videos.size,
        R.drawable.audio to audios.size
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        mediaTypes.forEach { mediaType ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(mediaType.first),
                    contentDescription = "Media",
                    modifier = Modifier
                        .size(14.dp)
                        .graphicsLayer(alpha = 0.99f)
                        .drawGradient()
                )

                Text(
                    text = "${mediaType.second}",
                    style = Typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun InteractionInformation(
    post: PostEntityFirebase,
    scope: CoroutineScope,
    reactionViewModelFirebase: ReactionViewModelFirebase,
    commentViewModelFirebase: CommentViewModelFirebase,
    shareViewModelFirebase: ShareViewModelFirebase
) {

    val reactions by remember(post.id) {
        reactionViewModelFirebase.getReactionsCountByPostId(post.id)
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), 0)
    }.collectAsState(initial = 0)

    val comments by remember(post.id) {
        commentViewModelFirebase.getCommentCountByPostId(post.id)
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), 0)
    }.collectAsState(initial = 0)

    val shares by remember(post.id) {
        shareViewModelFirebase.getShareCountByPostId(post.id)
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), 0)
    }.collectAsState(initial = 0)

    val mediaTypes = listOf(
        Triple(R.drawable.love, reactions, HeartColor),
        Triple(R.drawable.comment, comments, Color.White),
        Triple(R.drawable.share, shares, Color.White)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        mediaTypes.forEach { mediaType ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(mediaType.first),
                    contentDescription = "Interaction",
                    modifier = Modifier.size(14.dp),
                    tint = mediaType.third
                )

                Text(
                    text = "${mediaType.second}",
                    style = Typography.bodySmall
                )
            }
        }
    }
}