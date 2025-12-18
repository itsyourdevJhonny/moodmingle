package com.emc.moodmingle.ui.screens

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.UserEntityFirebase
import com.emc.moodmingle.ui.post.MultimediaCard
import com.emc.moodmingle.ui.post.PostMedia
import com.emc.moodmingle.ui.post.text.ExpandableAutoDetectClickableText
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.BrushSecondaryTertiaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import com.emc.moodmingle.viewmodel.firebase.HideViewModelFirebase
import com.emc.moodmingle.viewmodel.firebase.PostViewModelFirebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MoodFeedScreen(
    onCreateClick: () -> Unit,
    onClick: (String) -> Unit,
    onChatClick: (String, String) -> Unit
) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val postViewModel = hiltViewModel<PostViewModelFirebase>()
    val hideViewModel = hiltViewModel<HideViewModelFirebase>()

    val hiddenPosts by hideViewModel.userHides.collectAsState()

    var selectedMoodText by remember { mutableStateOf("All") }

    LaunchedEffect(selectedMoodText) {
        postViewModel.getFilteredPostsByMood(selectedMoodText)
    }

    val filteredPosts by postViewModel.filteredPosts.collectAsState()
    val allPosts by postViewModel.getAllPosts().collectAsState(initial = emptyList())

    val posts = if (selectedMoodText == "All") allPosts else filteredPosts

    val listState = rememberLazyListState()
    var pageSize by rememberSaveable { mutableIntStateOf(10) }
    var isLoadingMore by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(listState, posts, pageSize) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null &&
                    lastVisibleIndex >= pageSize - 3 &&
                    !isLoadingMore &&
                    pageSize < posts.size
                ) {
                    isLoadingMore = true
                    delay(300)
                    pageSize += 10
                    isLoadingMore = false
                }
            }
    }

    val pagedPosts = posts.take(pageSize)

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                FindSimilarMood(selectedMoodText) { selected ->
                    selectedMoodText = selected
                    if (selected != "All") {
                        postViewModel.getFilteredPostsByMood(selected)
                    }
                }
            }

            item {
                CreatePostSection(onCreateClick)
            }

            items(
                items = pagedPosts,
                key = { it.id }
            ) { post ->
                if (hiddenPosts.any { it.postId == post.id }) return@items

                var showShareSheet by remember { mutableStateOf(false) }
                var userEntity by remember { mutableStateOf<UserEntityFirebase?>(null) }

                LaunchedEffect(post.userId) {
                    userEntity = userViewModel.getUserByUid(post.userId).first().getOrNull()
                }

                if (post.urls.isNotEmpty()) {
                    MultimediaCard(
                        composable = {
                            PostMedia(
                                mediaUrls = post.urls,
                                onShowShareSheet = { showShareSheet = it }
                            )
                        },
                        postEntity = post,
                        userEntity = userEntity,
                        postType = "IMAGE",
                        onClick = onClick,
                        showShareSheet = showShareSheet,
                        onShowShareSheet = { showShareSheet = it },
                        onChatClick = onChatClick
                    )
                } else {
                    MultimediaCard(
                        composable = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 8.dp)
                            ) {
                                ExpandableAutoDetectClickableText(
                                    fullText = post.description,
                                    style = MaterialTheme.typography.bodyLarge,
                                    hasPadding = false
                                )
                            }
                        },
                        postEntity = post,
                        userEntity = userEntity,
                        postType = "TEXT",
                        onClick = onClick,
                        showShareSheet = showShareSheet,
                        onShowShareSheet = { showShareSheet = it },
                        onChatClick = onChatClick
                    )
                }
            }

            if (isLoadingMore) {
                item {
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

        FloatingRefreshButton(listState, onRefresh = { postViewModel.loadPosts() })
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FloatingRefreshButton(listState: LazyListState, onRefresh: suspend () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    val vibrator = context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as Vibrator

    val isVisible by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            offsetX = 0f
            offsetY = 0f
        }
    }

    var rotate by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (rotate) 360f else 0f,
        animationSpec = tween(600)
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(300)
    )

    val scale by animateFloatAsState(
        targetValue = if (listState.isScrollInProgress) 0.85f else 1f,
        animationSpec = tween(250)
    )

    val bounceOffsetY by animateFloatAsState(
        targetValue = offsetY,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    if (alpha <= 0f) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), bounceOffsetY.roundToInt()) }
                .graphicsLayer(alpha = alpha, rotationZ = rotation, scaleX = scale, scaleY = scale)
                .size(60.dp)
                .clip(CircleShape)
                .background(BrushPrimaryGradient)
                .combinedClickable(
                    onClick = {
                        rotate = true
                        scope.launch {
//                            scrollState.animateScrollTo(0)
                            listState.animateScrollToItem(0)
                            delay(200)
                            onRefresh()
                            rotate = false
                        }
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        vibrator.vibrate(
                            VibrationEffect.createOneShot(
                                80,
                                VibrationEffect.DEFAULT_AMPLITUDE
                            )
                        )
                    },
                    onDoubleClick = {
                        scope.launch { /*scrollState.animateScrollTo(0)*/
                            listState.animateScrollToItem(0)
                        }
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                )
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(id = R.drawable.refresh),
                contentDescription = "REFRESH",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun FindSimilarMood(selectedMoodText: String, onMoodSelected: (String) -> Unit) {
    val moods = getMoods()
    var showDialog by remember { mutableStateOf(false) }
    var value by remember { mutableStateOf("") }

    val filteredMoods = remember(value) {
        if (value.isBlank()) moods
        else moods.filter {
            it.second.contains(value, ignoreCase = true) || it.first.contains(value)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Find similar mood",
                style = MaterialTheme.typography.titleSmall.copy(color = GrayTextColor)
            )

            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.White,
                modifier = Modifier.clickable { showDialog = true }
            )
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                MoodText(mood = "🔄", moodText = "All", selectedMoodText, onMoodSelected)
            }
            items(moods) { mood ->
                MoodText(
                    mood = mood.first,
                    moodText = mood.second,
                    selectedMoodText,
                    onMoodSelected
                )
            }
        }

        if (showDialog) {
            Dialog(
                onDismissRequest = { showDialog = false }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                        .background(PrimaryDark)
                        .clip(RoundedCornerShape(30.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                modifier = Modifier
                                    .graphicsLayer(alpha = 0.99f)
                                    .drawGradient()
                            )
                            Text(
                                text = "Search Mood",
                                color = Color.White
                            )
                        }

                        TextField(
                            value = value,
                            onValueChange = { value = it },
                            placeholder = {
                                Text(
                                    text = "Search what you feel...",
                                    fontSize = 14.sp
                                )
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BrushPrimaryGradient, CircleShape),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (filteredMoods.isEmpty()) {
                                item {
                                    Text(
                                        text = "No results found",
                                        color = Color.Gray,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            } else {
                                items(filteredMoods) { mood ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onMoodSelected(mood.second)
                                                showDialog = false
                                                value = ""
                                            }
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = mood.first,
                                            fontSize = 22.sp
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = mood.second,
                                            style = MaterialTheme.typography.bodyMedium.copy(color = GrayTextColor)
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

@Composable
fun MoodText(
    mood: String,
    moodText: String,
    selectedMoodText: String,
    onMoodSelected: (String) -> Unit
) {
    val isSelected = moodText == selectedMoodText

    Box(
        modifier = Modifier
            .width(80.dp)
            .height(50.dp)
            .background(
                if (isSelected) BrushPrimaryGradient else BrushSecondaryTertiaryGradient,
                RoundedCornerShape(8.dp)
            )
            .clickable { onMoodSelected(moodText) },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = mood,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White,
                )
            )

            Text(
                text = moodText,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Composable
fun CreatePostSection(onCreateClick: () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(BrushPrimaryGradient, RoundedCornerShape(8.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        onClick = onCreateClick
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
            tint = Color.White
        )
        Text(text = "Share what you feel", color = Color.White)
    }
}

@Composable
fun LoadingMorePosts() {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        text = "Loading more post...",
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        ),
        textAlign = TextAlign.Center,
        color = Color.Gray
    )
}