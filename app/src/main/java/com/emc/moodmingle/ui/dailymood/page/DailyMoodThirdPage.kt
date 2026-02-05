package com.emc.moodmingle.ui.dailymood.page

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodEntity
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodText
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.ui.create.AllMediaGallery
import com.emc.moodmingle.ui.dailymood.dialog.DailyMoodTextDialog
import com.emc.moodmingle.ui.dailymood.image.DailyMoodEditImage
import com.emc.moodmingle.ui.remix.MoodPickerDialog
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.AnnotatedHashtag
import com.emc.moodmingle.utils.components.AnnotatedMention
import com.emc.moodmingle.utils.components.ScaffoldHeader
import com.emc.moodmingle.utils.media.image.ImageFilterType
import com.emc.moodmingle.utils.text.toColor
import com.emc.moodmingle.utils.text.toColorFilter
import com.emc.moodmingle.utils.text.toFontFamily
import com.emc.moodmingle.utils.text.toTextAlign
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import kotlin.math.roundToInt

@Composable
fun DailyMoodThirdPage(
    dailyMood: DailyMoodEntity,
    onDailyMoodEdited: (DailyMoodEntity) -> Unit,
    onTextPositionChanged: (DailyMoodText) -> Unit,
    onBack: () -> Unit
) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val currentUser by userViewModel.loggedUser

    var selectedAction by remember { mutableStateOf("") }

    BackHandler { onBack() }

    Box {
        Scaffold(
            containerColor = Color.Black,
            topBar = {
                ScaffoldHeader(
                    title = "Create Daily Mood",
                    enabled = dailyMood.text.description.isNotEmpty() || dailyMood.media.urls.isNotEmpty(),
                    onBack = onBack
                )
            },
            bottomBar = { Footer(currentUser) },
            floatingActionButton = { Actions(dailyMood) { selectedAction = it } }
        ) { paddingValues ->
            Content(paddingValues, dailyMood, onTextPositionChanged) { selectedAction = it }
        }

        when (selectedAction) {
            "mood" -> {
                MoodPickerDialog(
                    selectedMood = dailyMood.mood,
                    onSelectedMood = { onDailyMoodEdited(dailyMood.copy(mood = it)) },
                    onDismiss = { selectedAction = "" }
                )
            }

            "text" -> {
                DailyMoodTextDialog(
                    dailyMood,
                    onTextEdited = onDailyMoodEdited,
                    onDismiss = { selectedAction = "" }
                )
            }

            "media" -> {
                AllMediaGallery(
                    mediaUris = dailyMood.media.urls.map { it.toUri() },
                    onSelectedType = {},
                    onUploadedUri = { uris ->
                        onDailyMoodEdited(dailyMood.copy(media = dailyMood.media.copy(urls = uris.map { it.toString() })))
                    },
                    onDismiss = { selectedAction = "" }
                )
            }

            "edit_image" -> {
                DailyMoodEditImage(dailyMood, onDailyMoodEdited) { selectedAction = "" }
            }
        }
    }
}

@Composable
private fun Footer(currentUser: UserEntityFirebase?) {
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
                IconButton(
                    onClick = {},
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = SecondaryDark,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null
                    )
                }

                TextButton(
                    onClick = {},
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White,
                        containerColor = SecondaryDark
                    )
                ) {
                    Text(
                        text = "Share with"
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(R.drawable.public_world),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            TextButton(
                onClick = {},
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
    }
}

@Composable
private fun Actions(dailyMood: DailyMoodEntity, onActionSelected: (String) -> Unit) {
    val actions = getActions()

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        actions.forEach { (label, icon) ->
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(PrimaryDark, CircleShape)
                    .clickable { onActionSelected(label) },
                contentAlignment = Alignment.Center
            ) {
                when {
                    label == "mood" && dailyMood.mood.description.isNotBlank() -> {
                        Text(text = dailyMood.mood.emoji, fontSize = 28.sp, color = Color.White)
                    }

                    label == "music" && dailyMood.musicTrack != null -> {
                        AsyncImage(
                            model = dailyMood.musicTrack.streamUrl,
                            contentDescription = "Music",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                        )
                    }

                    else -> {
                        Icon(
                            painter = painterResource(icon),
                            contentDescription = "Action",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    dailyMood: DailyMoodEntity,
    onTextPositionChanged: (DailyMoodText) -> Unit,
    onActionSelected: (String) -> Unit
) {
    var boxSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .onGloballyPositioned { coords -> boxSize = coords.size }
    ) {
        if (dailyMood.media.urls.isNotEmpty()) {
            ImageSection(dailyMood, boxSize, onTextPositionChanged, onActionSelected)
        }

        if (dailyMood.text.description.isNotBlank()) {
            DescriptionSection(dailyMood, boxSize, onTextPositionChanged, onActionSelected)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .animateContentSize()
        ) {
            Column(
                modifier = Modifier.widthIn(max = 212.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (dailyMood.text.mentions.isNotEmpty()) {
                    AnnotatedMention(mentions = dailyMood.text.mentions)
                }

                if (dailyMood.text.hashtag.isNotBlank() && dailyMood.text.hashtag != "#") {
                    AnnotatedHashtag(hashtag = dailyMood.text.hashtag)
                }
            }

            if (dailyMood.mood.description.isNotBlank()) {
                MoodSection(dailyMood)
            }
        }
    }
}

@Composable
private fun MoodSection(dailyMood: DailyMoodEntity) {
    Box(
        modifier = Modifier
            .background(BrushPrimaryGradient, RoundedCornerShape(8.dp))
            .padding(8.dp)
            .animateContentSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = dailyMood.mood.emoji, fontSize = 20.sp, color = Color.White)

            Text(
                text = dailyMood.mood.description,
                style = Typography.bodyMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
private fun ImageSection(
    dailyMood: DailyMoodEntity,
    boxSize: IntSize,
    onTextPositionChanged: (DailyMoodText) -> Unit,
    onActionSelected: (String) -> Unit
) {
    val imageFilterName = dailyMood.media.imageFilterName

    var imageSize by remember { mutableStateOf(IntSize.Zero) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    var isPositionInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(offsetX, offsetY) {
        onTextPositionChanged(dailyMood.text.copy(offsetX = offsetX, offsetY = offsetY))
    }

    AsyncImage(
        model = dailyMood.media.urls[0],
        contentDescription = null,
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
            .clickable { onActionSelected("edit_image") }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()

                    val newX = offsetX + dragAmount.x
                    val newY = offsetY + dragAmount.y

                    val maxX = (boxSize.width - imageSize.width).coerceAtLeast(0)
                    val maxY = (boxSize.height - imageSize.height).coerceAtLeast(0)

                    offsetX = newX.coerceIn(0f, maxX.toFloat())
                    offsetY = newY.coerceIn(0f, maxY.toFloat())
                }
            },
        colorFilter = if (imageFilterName != ImageFilterType.NORMAL.name) imageFilterName.toColorFilter() else null
    )
}

@Composable
private fun DescriptionSection(
    dailyMood: DailyMoodEntity,
    boxSize: IntSize,
    onTextPositionChanged: (DailyMoodText) -> Unit,
    onActionSelected: (String) -> Unit
) {
    val dailyMoodText = dailyMood.text

    var columnSize by remember { mutableStateOf(IntSize.Zero) }

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    var isPositionInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(offsetX, offsetY) {
        onTextPositionChanged(dailyMoodText.copy(offsetX = offsetX, offsetY = offsetY))
    }

    Box(
        modifier = Modifier
            .onGloballyPositioned { coords ->
                columnSize = coords.size

                if (!isPositionInitialized && boxSize != IntSize.Zero) {
                    offsetX = (boxSize.width - columnSize.width) / 2f
                    offsetY = (boxSize.height - columnSize.height) / 2f
                    isPositionInitialized = true
                }
            }
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .clickable { onActionSelected("text") }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()

                    val newX = offsetX + dragAmount.x
                    val newY = offsetY + dragAmount.y

                    val maxX = (boxSize.width - columnSize.width).coerceAtLeast(0)
                    val maxY = (boxSize.height - columnSize.height).coerceAtLeast(0)

                    offsetX = newX.coerceIn(0f, maxX.toFloat())
                    offsetY = newY.coerceIn(0f, maxY.toFloat())
                }
            }
    ) {
        Row(modifier = Modifier.widthIn(max = 232.dp)) {
            Text(
                text = "“${dailyMoodText.description}”",
                color = dailyMoodText.color.toColor(),
                fontFamily = dailyMoodText.font.toFontFamily(),
                textAlign = dailyMoodText.align.toTextAlign()
            )
        }
    }
}

private fun getActions(): List<Pair<String, Int>> {
    return listOf(
        "mood" to R.drawable.mood,
        "text" to R.drawable.text_style,
        "media" to R.drawable.image_video,
        "music" to R.drawable.music_note,
        "location" to R.drawable.location
    )
}