package com.emc.moodmingle.ui.dailymood.page

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.dailymood.AudienceType
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodEntity
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodImage
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodMediaType
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodText
import com.emc.moodmingle.data.firebase.model.post.dailymood.Gif
import com.emc.moodmingle.data.firebase.model.post.dailymood.GifType
import com.emc.moodmingle.data.firebase.model.post.dailymood.ShapeType
import com.emc.moodmingle.data.firebase.model.post.dailymood.TextStyle
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.ui.create.detectLongPress
import com.emc.moodmingle.ui.create.getMimeType
import com.emc.moodmingle.ui.dailymood.action.DailyMoodContentBottomSheet
import com.emc.moodmingle.ui.dailymood.action.DailyMoodFloatingActions
import com.emc.moodmingle.ui.dailymood.hashtag.DailyMoodHashtagSection
import com.emc.moodmingle.ui.dailymood.location.DailyMoodLocation
import com.emc.moodmingle.ui.dailymood.media.image.DailyMoodEditImage
import com.emc.moodmingle.ui.dailymood.media.image.DailyMoodSelectMedia
import com.emc.moodmingle.ui.dailymood.media.image.animatedShape
import com.emc.moodmingle.ui.dailymood.mention.DailyMoodMentionSection
import com.emc.moodmingle.ui.dailymood.mood.DailyMoodMoodSection
import com.emc.moodmingle.ui.dailymood.text.DailyMoodEditText
import com.emc.moodmingle.ui.dailymood.visibility.DailyMoodAudience
import com.emc.moodmingle.ui.remix.MoodPickerDialog
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.GifPicker
import com.emc.moodmingle.utils.components.ScaffoldHeader
import com.emc.moodmingle.utils.media.MediaPaletteExtractor
import com.emc.moodmingle.utils.media.image.ImageFilterType
import com.emc.moodmingle.utils.media.video.editor.VideoEditor
import com.emc.moodmingle.utils.text.toColor
import com.emc.moodmingle.utils.text.toColorFilter
import com.emc.moodmingle.utils.text.toFontFamily
import com.emc.moodmingle.utils.text.toTextAlign
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun DailyMoodThirdPage(
    mood: DailyMoodEntity,
    onEdited: (DailyMoodEntity) -> Unit,
    onTextPositionChanged: (DailyMoodText) -> Unit,
    onImagePositionChanged: (DailyMoodImage) -> Unit,
    onGifPositionChanged: (Gif) -> Unit,
    onBack: () -> Unit,
) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val currentUser by userViewModel.loggedUser

    var selectedAction by remember { mutableStateOf("") }

    val isContentPaused = selectedAction == "edit_single_video"

    BackHandler { onBack() }

    Box {
        Scaffold(
            containerColor = Color.Black,
            topBar = {
                ScaffoldHeader(
                    title = "Create Daily Mood",
                    enabled = mood.text.description.isNotEmpty() || mood.media.urls.isNotEmpty(),
                    onBack = onBack
                )
            },
            bottomBar = { Footer(currentUser, mood) { selectedAction = it } },
            floatingActionButton = {
                DailyMoodFloatingActions(mood, selectedAction) { selectedAction = it }
            }
        ) { paddingValues ->
            Content(
                paddingValues,
                mood,
                onTextPositionChanged,
                onImagePositionChanged,
                onGifPositionChanged,
                onActionSelected = { selectedAction = it },
                onMentionDeleted = { onEdited(mood.copy(text = mood.text.copy(mentions = mood.text.mentions - it))) },
                isPaused = isContentPaused
            )
        }

        // Content
        when (selectedAction) {
            "mood" -> {
                MoodPickerDialog(
                    selectedMood = mood.mood,
                    onSelectedMood = { onEdited(mood.copy(mood = it)) },
                    onDismiss = { selectedAction = "" }
                )
            }

            "text" -> DailyMoodEditText(mood, onEdited) { selectedAction = "" }

            "media" -> {
                DailyMoodSelectMedia(
                    uris = mood.media.urls.map { it.toUri() },
                    selectMultiple = false,
                    onUrisSelected = { uris ->
                        onEdited(mood.copy(media = mood.media.copy(urls = uris.map { it.toString() })))
                    },
                    onDismiss = { selectedAction = "" }
                )
            }

            "edit_single_image" -> DailyMoodEditImage(mood, onEdited) { selectedAction = "" }

            "edit_single_video" -> {
                VideoEditor(
                    videoUri = mood.media.urls.first().toUri(),
                    onStateChanged = { onEdited(mood.copy(media = mood.media.copy(video = it))) },
                    onDismiss = { selectedAction = "" }
                )
            }

            "gif" -> GifPicker(mood, onEdited) { selectedAction = "" }

            "location" -> DailyMoodLocation(mood, onEdited) { selectedAction = "" }
        }

        // Footer
        when (selectedAction) {
            "audience" -> DailyMoodAudience(mood, onEdited) { selectedAction = "" }
            "settings" -> {}
            "upload" -> {}
        }
    }

    // Sheet
    when (selectedAction) {
        "text_sheet", "single_image_sheet", "single_video_sheet", "gif_sheet" -> {
            DailyMoodContentBottomSheet(
                selectedAction,
                mood,
                onEdited,
                onActionSelected = { selectedAction = it },
                onDismiss = { selectedAction = "" }
            )
        }
    }
}

@Composable
private fun Footer(
    currentUser: UserEntityFirebase?,
    mood: DailyMoodEntity,
    onActionSelected: (String) -> Unit,
) {
    BottomAppBar(
        contentPadding = PaddingValues(),
        containerColor = PrimaryDark,
        modifier = Modifier.clip(RoundedCornerShape(topEnd = 38.dp, topStart = 38.dp))
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SettingsButton(onActionSelected)
                AudienceButton(onActionSelected, mood)
            }

            UploadButton(currentUser, onActionSelected)
        }
    }
}

@Composable
private fun SettingsButton(onActionSelected: (String) -> Unit) {
    IconButton(
        onClick = { onActionSelected("settings") },
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = SecondaryDark,
            contentColor = Color.White
        )
    ) {
        Icon(imageVector = Icons.Default.Settings, contentDescription = null)
    }
}

@Composable
private fun AudienceButton(onActionSelected: (String) -> Unit, mood: DailyMoodEntity) {
    TextButton(
        onClick = { onActionSelected("audience") },
        colors = ButtonDefaults.textButtonColors(
            contentColor = Color.White,
            containerColor = SecondaryDark
        )
    ) {
        Text(text = "Share with")
        Spacer(Modifier.width(8.dp))
        Icon(
            painter = painterResource(
                when (mood.audience.type) {
                    AudienceType.PUBLIC -> R.drawable.public_world
                    AudienceType.PRIVATE -> R.drawable.private_user
                    AudienceType.FOLLOWERS -> R.drawable.followers
                    AudienceType.SUPPORTERS -> R.drawable.supporter
                    AudienceType.CUSTOM -> R.drawable.custom
                }
            ),
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun UploadButton(currentUser: UserEntityFirebase?, onActionSelected: (String) -> Unit) {
    TextButton(
        onClick = { onActionSelected("upload") },
        colors = ButtonDefaults.textButtonColors(
            contentColor = Color.White,
            containerColor = SecondaryDark
        )
    ) {
        AsyncImage(
            model = currentUser?.avatarUrl.orEmpty(),
            contentDescription = "Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
        )
        Spacer(Modifier.width(8.dp))
        Text(text = "Post", fontWeight = FontWeight.Black)
    }
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    mood: DailyMoodEntity,
    onTextPositionChanged: (DailyMoodText) -> Unit,
    onImagePositionChanged: (DailyMoodImage) -> Unit,
    onGifPositionChanged: (Gif) -> Unit,
    onActionSelected: (String) -> Unit,
    onMentionDeleted: (String) -> Unit,
    isPaused: Boolean,
) {
    val context = LocalContext.current
    var boxSize by remember { mutableStateOf(IntSize.Zero) }
    var paletteColors by remember { mutableStateOf(listOf(Color.Black, Color.Black)) }

    if (mood.media.type == DailyMoodMediaType.SINGLE && mood.media.urls.isNotEmpty()) {
        val uri = mood.media.urls.first().toUri()
        val mimeType = getMimeType(context, uri) ?: ""
        val isVideo = mimeType.startsWith("video")

        val (topColor, bottomColor) = rememberMediaPalette(uri, isVideo).value
        paletteColors = listOf(topColor, bottomColor)
    } else {
        paletteColors = listOf(Color.Black, Color.Black)
    }

    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(Brush.verticalGradient(paletteColors))
            .onGloballyPositioned { coords -> boxSize = coords.size }
    ) {
        if (mood.media.urls.isNotEmpty()) {
            val urls = mood.media.urls

            when (mood.media.type) {
                DailyMoodMediaType.SINGLE -> {
                    val mimeType = getMimeType(context, urls.first().toUri()) ?: ""

                    when {
                        mimeType.startsWith("image") -> {
                            SingleImageSection(
                                mood,
                                boxSize,
                                onImagePositionChanged,
                                onActionSelected
                            )
                        }

                        mimeType.startsWith("video") -> {
                            SingleVideoSection(context, mood, isPaused, onActionSelected)
                        }
                    }
                }

                DailyMoodMediaType.COLLAGE -> {}
                DailyMoodMediaType.LAYOUT -> {}
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .animateContentSize()
        ) {
            DailyMoodMoodSection(mood)
        }

        GifSection(mood, boxSize, onActionSelected, onGifPositionChanged)

        DescriptionSection(mood, boxSize, onTextPositionChanged, onActionSelected)

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 8.dp)
        ) {
            DailyMoodMentionSection(mood, onMentionDeleted)
            DailyMoodHashtagSection(mood)
            LocationSection(mood)
        }
    }
}

@Composable
private fun BoxScope.DescriptionSection(
    mood: DailyMoodEntity,
    boxSize: IntSize,
    onTextPositionChanged: (DailyMoodText) -> Unit,
    onActionSelected: (String) -> Unit,
) {
    val text = mood.text

    val color = if (text.color.toColor().luminance() < 0.5f) Color.White else Color.Black

    var size by remember { mutableStateOf(IntSize.Zero) }

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    var isPositionInitialized by remember { mutableStateOf(false) }

    val snapThresholdPx = with(LocalDensity.current) { 8.dp.toPx() }

    val centerX = (boxSize.width - size.width) / 2f
    val centerY = (boxSize.height - size.height) / 2f

    var showVerticalGuide by remember { mutableStateOf(false) }
    var showHorizontalGuide by remember { mutableStateOf(false) }

    LaunchedEffect(offsetX, offsetY) {
        onTextPositionChanged(text.copy(offsetX = offsetX, offsetY = offsetY))
    }

    AnimatedVisibility(
        visible = mood.text.description.isNotBlank(),
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier
            .onGloballyPositioned { coords ->
                size = coords.size

                if (!isPositionInitialized && boxSize != IntSize.Zero) {
                    offsetX = (boxSize.width - size.width) / 2f
                    offsetY = (boxSize.height - size.height) / 2f
                    isPositionInitialized = true
                }
            }
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .detectLongPress(
                onLongPress = { onActionSelected("text_sheet") },
                onTap = { onActionSelected("text") }
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        // SNAP WHEN RELEASED
                        if (abs(offsetX - centerX) <= snapThresholdPx) {
                            offsetX = centerX
                        }
                        if (abs(offsetY - centerY) <= snapThresholdPx) {
                            offsetY = centerY
                        }

                        showVerticalGuide = false
                        showHorizontalGuide = false
                    }
                ) { change, dragAmount ->
                    change.consume()

                    var newX = offsetX + dragAmount.x
                    var newY = offsetY + dragAmount.y

                    val maxX = (boxSize.width - size.width).coerceAtLeast(0)
                    val maxY = (boxSize.height - size.height).coerceAtLeast(0)

                    // CENTER DETECTION
                    showVerticalGuide = abs(newX - centerX) <= snapThresholdPx
                    showHorizontalGuide = abs(newY - centerY) <= snapThresholdPx

                    // MAGNET EFFECT (SOFT SNAP WHILE DRAGGING)
                    if (showVerticalGuide) {
                        newX = centerX
                    }
                    if (showHorizontalGuide) {
                        newY = centerY
                    }

                    offsetX = newX.coerceIn(0f, maxX.toFloat())
                    offsetY = newY.coerceIn(0f, maxY.toFloat())
                }
            }
    ) {
        Text(
            text = text.description,
            color = when (text.style) {
                TextStyle.NORMAL -> text.color.toColor()
                TextStyle.WITH_BACKGROUND -> color
                TextStyle.WITHOUT_BACKGROUND -> Color.White
            },
            fontFamily = text.font.toFontFamily(),
            textAlign = text.align.toTextAlign(),
            modifier = Modifier
                .widthIn(max = (boxSize.width - 390).dp)
                .background(
                    color = when (text.style) {
                        TextStyle.NORMAL -> Color.Transparent
                        TextStyle.WITH_BACKGROUND -> text.color.toColor()
                        TextStyle.WITHOUT_BACKGROUND -> text.color.toColor().copy(alpha = 0.3f)
                    },
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        )
    }

    AnimatedDivider(visible = showVerticalGuide) {
        VerticalDivider(color = Color.White.copy(alpha = 0.8f))
    }

    AnimatedDivider(visible = showHorizontalGuide) {
        HorizontalDivider(color = Color.White.copy(alpha = 0.8f))
    }
}

@Composable
private fun BoxScope.SingleImageSection(
    mood: DailyMoodEntity,
    boxSize: IntSize,
    onImagePositionChanged: (DailyMoodImage) -> Unit,
    onActionSelected: (String) -> Unit,
) {
    val imageFilterName = mood.media.image.filterName
    val isShapeNormal = mood.media.image.shapeType == ShapeType.NORMAL

    var imageSize by remember { mutableStateOf(IntSize.Zero) }

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    var isPositionInitialized by remember { mutableStateOf(false) }

    val snapThresholdPx = with(LocalDensity.current) { 8.dp.toPx() }

    val centerX = (boxSize.width - imageSize.width) / 2f
    val centerY = (boxSize.height - imageSize.height) / 2f

    var showVerticalGuide by remember { mutableStateOf(false) }
    var showHorizontalGuide by remember { mutableStateOf(false) }

    LaunchedEffect(offsetX, offsetY) {
        onImagePositionChanged(mood.media.image.copy(offsetX = offsetX, offsetY = offsetY))
    }

    AsyncImage(
        model = mood.media.urls.first(),
        contentDescription = null,
        contentScale = if (isShapeNormal) ContentScale.Fit else ContentScale.Crop,
        modifier = Modifier
            .onGloballyPositioned { coords ->
                imageSize = coords.size

                if (!isPositionInitialized && imageSize.height < boxSize.height) {
                    offsetX = 0.0f
                    offsetY = 340.5f
                    isPositionInitialized = true
                }

                if (!isPositionInitialized && imageSize.width < boxSize.width) {
                    offsetX = 100.0f
                    offsetY = 0.0f
                    isPositionInitialized = true
                }
            }
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .detectLongPress(
                onLongPress = { onActionSelected("single_image_sheet") },
                onTap = { onActionSelected("edit_single_image") }
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        if (isShapeNormal) {
                            // SNAP WHEN RELEASED
                            if (abs(offsetX - centerX) <= snapThresholdPx) {
                                offsetX = centerX
                            }
                            if (abs(offsetY - centerY) <= snapThresholdPx) {
                                offsetY = centerY
                            }

                            showVerticalGuide = false
                            showHorizontalGuide = false
                        }
                    }
                ) { change, dragAmount ->
                    change.consume()

                    var newX = offsetX + dragAmount.x
                    var newY = offsetY + dragAmount.y

                    val maxX = (boxSize.width - imageSize.width).coerceAtLeast(0)
                    val maxY = (boxSize.height - imageSize.height).coerceAtLeast(0)

                    if (isShapeNormal) {
                        // CENTER DETECTION
                        showVerticalGuide = abs(newX - centerX) <= snapThresholdPx
                        showHorizontalGuide = abs(newY - centerY) <= snapThresholdPx

                        // MAGNET EFFECT (SOFT SNAP WHILE DRAGGING)
                        if (showVerticalGuide) newX = centerX
                        if (showHorizontalGuide) newY = centerY
                    }

                    offsetX = newX.coerceIn(0f, maxX.toFloat())
                    offsetY = newY.coerceIn(0f, maxY.toFloat())
                }
            }
            .size(if (mood.media.image.shapeType != ShapeType.NORMAL) 260.dp else Dp.Unspecified)
            .clip(animatedShape(mood.media.image.shapeType)),
        colorFilter = if (imageFilterName != ImageFilterType.NORMAL.name) imageFilterName.toColorFilter() else null
    )

    if (isShapeNormal) {
        AnimatedDivider(visible = showVerticalGuide) {
            VerticalDivider(color = Color.White.copy(alpha = 0.8f))
        }

        AnimatedDivider(visible = showHorizontalGuide) {
            HorizontalDivider(color = Color.White.copy(alpha = 0.8f))
        }
    }
}

@Composable
private fun BoxScope.SingleVideoSection(
    context: Context,
    mood: DailyMoodEntity,
    isPaused: Boolean,
    onActionSelected: (String) -> Unit,
) {
    val urls = mood.media.urls

    val exoPlayer = remember { ExoPlayer.Builder(context).build() }
    exoPlayer.setMediaItem(MediaItem.fromUri(urls.first()))
    exoPlayer.prepare()
    exoPlayer.playWhenReady = true
    exoPlayer.repeatMode = Player.REPEAT_MODE_ONE

    // When isPaused is true, pause the player. When false, play it.
    LaunchedEffect(isPaused) { if (isPaused) exoPlayer.pause() else exoPlayer.play() }

    DisposableEffect(Unit) { onDispose { exoPlayer.release() } }

    Log.d("TAG", "Composed")

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = false
            }
        },
        modifier = Modifier
            .align(Alignment.Center)
            .detectLongPress(
                onLongPress = { onActionSelected("single_video_sheet") },
                onTap = { onActionSelected("edit_single_video") }
            )
    )
}

@Composable
private fun rememberMediaPalette(uri: Uri, isVideo: Boolean): State<Pair<Color, Color>> {
    val context = LocalContext.current
    val state = remember { mutableStateOf(Color.Black to Color.Black) }

    LaunchedEffect(uri) {
        state.value = MediaPaletteExtractor.extractTopBottomColors(context, uri, isVideo)
    }

    return state
}

@OptIn(UnstableApi::class)
@Composable
private fun BoxScope.GifSection(
    mood: DailyMoodEntity,
    boxSize: IntSize,
    onActionSelected: (String) -> Unit,
    onGifPositionChanged: (Gif) -> Unit,
) {
    var size by remember { mutableStateOf(IntSize.Zero) }

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    var isPositionInitialized by remember { mutableStateOf(false) }

    val snapThresholdPx = with(LocalDensity.current) { 8.dp.toPx() }

    val centerX = (boxSize.width - size.width) / 2f
    val centerY = (boxSize.height - size.height) / 2f

    var showVerticalGuide by remember { mutableStateOf(false) }
    var showHorizontalGuide by remember { mutableStateOf(false) }

    LaunchedEffect(offsetX, offsetY) {
        onGifPositionChanged(mood.gif.copy(offsetX = offsetX, offsetY = offsetY))
    }

    LaunchedEffect(mood.gif.url) {
        offsetX = centerX
        offsetY = centerY
    }

    key(mood.gif.url) {
        AnimatedVisibility(
            visible = mood.gif.url.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .onGloballyPositioned { coords ->
                    size = coords.size

                    if (!isPositionInitialized && boxSize != IntSize.Zero) {
                        offsetX = (boxSize.width - size.width) / 2f
                        offsetY = (boxSize.height - size.height) / 2f
                        isPositionInitialized = true
                    }
                }
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) { detectTapGestures(onPress = { onActionSelected("gif_sheet") }) }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            // SNAP WHEN RELEASED
                            if (abs(offsetX - centerX) <= snapThresholdPx) offsetX = centerX
                            if (abs(offsetY - centerY) <= snapThresholdPx) offsetY = centerY

                            showVerticalGuide = false
                            showHorizontalGuide = false
                        }
                    ) { change, dragAmount ->
                        change.consume()

                        var newX = offsetX + dragAmount.x
                        var newY = offsetY + dragAmount.y

                        val maxX = (boxSize.width - size.width).coerceAtLeast(0)
                        val maxY = (boxSize.height - size.height).coerceAtLeast(0)

                        // CENTER DETECTION
                        showVerticalGuide = abs(newX - centerX) <= snapThresholdPx
                        showHorizontalGuide = abs(newY - centerY) <= snapThresholdPx

                        // MAGNET EFFECT (SOFT SNAP WHILE DRAGGING)
                        if (showVerticalGuide) newX = centerX
                        if (showHorizontalGuide) newY = centerY

                        offsetX = newX.coerceIn(0f, maxX.toFloat())
                        offsetY = newY.coerceIn(0f, maxY.toFloat())
                    }
                }
        ) {
            when (mood.gif.type) {
                GifType.IMAGE -> {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(mood.gif.url)
                            .crossfade(true)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }

                GifType.VIDEO -> {
                    val context = LocalContext.current
                    val exoPlayer = remember(mood.gif.url) {
                        ExoPlayer.Builder(context)
                            .build()
                            .apply {
                                setMediaItem(MediaItem.fromUri(mood.gif.url))
                                prepare()
                                playWhenReady = true
                                repeatMode = Player.REPEAT_MODE_ONE
                                volume = 0f
                            }
                    }

                    DisposableEffect(mood.gif.url) { onDispose { exoPlayer.release() } }

                    AndroidView(
                        factory = {
                            PlayerView(context).apply {
                                player = exoPlayer
                                useController = false
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                            }
                        },
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }
        }
    }

    AnimatedDivider(visible = showVerticalGuide) {
        VerticalDivider(color = Color.White.copy(alpha = 0.8f))
    }

    AnimatedDivider(visible = showHorizontalGuide) {
        HorizontalDivider(color = Color.White.copy(alpha = 0.8f))
    }
}

@Composable
private fun LocationSection(mood: DailyMoodEntity) {
    AnimatedVisibility(
        visible = mood.location != null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        mood.location?.let { location ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.location),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )

                Text(
                    text = location.displayName,
                    style = Typography.bodyMedium.copy(color = Color.White)
                )
            }
        }
    }
}

@Composable
private fun BoxScope.AnimatedDivider(
    visible: Boolean,
    content: @Composable (AnimatedVisibilityScope.() -> Unit),
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier.align(Alignment.Center),
        content = content
    )
}