package com.emc.moodmingle.ui.screens

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.UserEntityFirebase
import com.emc.moodmingle.service.UploadPostService
import com.emc.moodmingle.ui.create.VideoThumbnail
import com.emc.moodmingle.ui.create.getMimeType
import com.emc.moodmingle.ui.post.action.DrawLine
import com.emc.moodmingle.ui.post.action.formatText
import com.emc.moodmingle.ui.post.audio.AudioItemMini
import com.emc.moodmingle.ui.post.audio.FilePicker
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.PurpleDark
import com.emc.moodmingle.ui.theme.PurplePrimary
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel

@Composable
fun CreatePostScreen(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CreateTopBar(onBackClick)
        CreateMainContent(onBackClick)
    }
}

@Composable
fun CreateTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrushPrimaryGradient)
            .padding(top = 20.dp),
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = "Back Icon",
                tint = Color.White
            )
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            text = "Create Post",
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CreateMainContent(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var mood by remember { mutableStateOf("") }
    var moodEmoji by remember { mutableStateOf("") }
    var hashtag by remember { mutableStateOf("") }
    var caption by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var uris by remember { mutableStateOf<List<Uri?>>(emptyList()) }
    var isChecked by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 42.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.TopStart)
                        .graphicsLayer(alpha = 0.99f)
                        .drawGradient()
                )
            }

            SwitchButton(isChecked, onCheckedChange = { isChecked = it })

            CreateTitle(text = "Choose your mood")

            CreateMoods(
                selectedMoodEmoji = moodEmoji,
                onSelectedMoodEmoji = { moodEmoji = it },
                onSelectedMood = { mood = it }
            )

            CreateTitle(text = "Hashtag")
            CreateHashTag(onCreateHashtag = { hashtag = it })

            CreateTitle(text = "Short caption about your mood")
            InformationButton(text = caption, label = "Caption", onValueChange = { caption = it })

            CreateTitle(text = "Describe what you feel")
            InformationButton(
                text = description,
                label = "Description",
                onValueChange = { description = it }
            )

            AnimatedContent(
                targetState = uris.isNotEmpty(),
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(
                        animationSpec = tween(300)
                    )
                }
            ) { hasItems ->
                if (hasItems) {
                    val mediaPlayers = remember { mutableStateMapOf<Uri, MediaPlayer>() }
                    var currentlyPlaying by remember { mutableStateOf<Uri?>(null) }

                    DisposableEffect(Unit) {
                        onDispose {
                            mediaPlayers.values.forEach { it.release() }
                        }
                    }

                    LazyRow(
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(uris, key = { it.toString() }) { uri ->
                            val isPlaying = currentlyPlaying == uri

                            Box(modifier = Modifier.size(66.dp)) {
                                val mimeType = getMimeType(context, uri!!) ?: ""

                                when {
                                    mimeType.startsWith("image") -> {
                                        Image(
                                            painter = rememberAsyncImagePainter(
                                                model = uri,
                                                placeholder = painterResource(R.drawable.image),
                                                error = painterResource(R.drawable.image)
                                            ),
                                            contentDescription = "Selected image",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(8.dp))
                                                .border(
                                                    width = 1.dp,
                                                    brush = BrushPrimaryGradient,
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                        )
                                    }

                                    mimeType.startsWith("video") -> {
                                        VideoThumbnail(videoUri = uri)
                                    }

                                    mimeType.startsWith("audio") -> {
                                        AudioItemMini(
                                            uri = uri,
                                            isPlaying = isPlaying,
                                            onClickPlay = {
                                                currentlyPlaying?.let { prev ->
                                                    mediaPlayers[prev]?.pause()
                                                }
                                                val mp = mediaPlayers.getOrPut(uri) {
                                                    MediaPlayer.create(context, uri)
                                                }
                                                mp.start()
                                                currentlyPlaying = uri
                                            },
                                            onClickPause = {
                                                mediaPlayers[uri]?.pause()
                                                currentlyPlaying = null
                                            }
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { uris = uris - uri },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }

                        if (uris.isNotEmpty() && uris.size > 1) {
                            item {
                                IconButton(
                                    onClick = { uris = emptyList() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove all",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }
                } else {
                    FilePicker(
                        onSelectedType = { selectedType = it },
                        onUploadedUri = { uris = it }
                    )
                }
            }
        }

        CreatePostButton(
            mood = mood,
            moodEmoji = moodEmoji,
            hashtag = hashtag,
            caption = caption,
            description = description,
            type = selectedType,
            uris = uris,
            onBackClick = onBackClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMoods(
    selectedMoodEmoji: String,
    onSelectedMoodEmoji: (String) -> Unit,
    onSelectedMood: (String) -> Unit
) {
    var selected by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(top = 10.dp)
            .border(1.dp, if (selected) PurplePrimary else Color.Gray, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (selected && selectedMoodEmoji != "") {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .clip(CircleShape)
                    .clickable { showDialog = true }
            ) {
                Text(
                    text = selectedMoodEmoji,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        Color.White
                    )
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { showDialog = true },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    modifier = Modifier
                        .graphicsLayer(alpha = 0.99f)
                        .drawWithCache {
                            onDrawWithContent {
                                drawContent()
                                drawRect(
                                    brush = BrushPrimaryGradient,
                                    blendMode = BlendMode.SrcAtop
                                )
                            }
                        },
                    contentDescription = "Select",
                )

                Text(
                    text = "Select",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                )
            }
        }
    }

    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false),
            content = {
                DisplayMoods(
                    selectedMoodEmoji = selectedMoodEmoji,
                    onSelectedMoodEmoji = onSelectedMoodEmoji,
                    onSelectedMood = onSelectedMood,
                    onSelected = { selected = it },
                    onShowDialog = { showDialog = it }
                )
            }
        )
    }
}

@Composable
fun DisplayMoods(
    selectedMoodEmoji: String,
    onSelectedMoodEmoji: (String) -> Unit,
    onSelectedMood: (String) -> Unit,
    onSelected: (Boolean) -> Unit,
    onShowDialog: (Boolean) -> Unit
) {
    val moods = getMoods()

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        items(moods) { (emoji, text) ->
            val isSelected = emoji == selectedMoodEmoji

            Box(
                modifier = Modifier
                    .size(70.dp)
                    .padding(4.dp)
                    .background(
                        color = SecondaryDark,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) PurpleDark else PurplePrimary.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        if (isSelected) onSelectedMoodEmoji("") else onSelectedMoodEmoji(emoji)
                        if (isSelected) onSelectedMood("") else onSelectedMood(text)
                        onSelected(!isSelected)
                        onShowDialog(false)
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = emoji, fontSize = 24.sp)
                    Text(text = text, color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun CreateTitle(text: String) {
    val isFromMood = text == "Choose your mood"
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = if (isFromMood) 0.dp else 16.dp,
                bottom = if (isFromMood) 0.dp else 4.dp,
                start = if (isFromMood) 0.dp else 12.dp
            ),
        text = text,
        style = MaterialTheme.typography.titleSmall.copy(
            textAlign = if (isFromMood) TextAlign.Center else TextAlign.Unspecified
        )
    )
}

@Composable
fun CreateHashTag(onCreateHashtag: (String) -> Unit) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
//        label = { Text(text = "#") },
        value = textFieldValue,
        singleLine = true,
        onValueChange = { newText ->
            textFieldValue = if (newText.text.startsWith("#")) {
                newText.copy(selection = TextRange(newText.text.length))
            } else {
                TextFieldValue(
                    text = "#${newText.text}",
                    selection = TextRange(newText.text.length + 1)
                )
            }

            onCreateHashtag(textFieldValue.text)
        },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedLabelColor = GrayTextColor,
            focusedLabelColor = Color.White,
            focusedBorderColor = PurplePrimary,
            focusedTextColor = Color.White
        ),
        shape = RoundedCornerShape(30.dp),
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.hashtag),
                contentDescription = "Hashtag",
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient()
            )
        }
    )
}

@Composable
fun InformationButton(text: String, label: String, onValueChange: (String) -> Unit) {
    var showDescriptionDialog by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }

    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    var showUserDialog by remember { mutableStateOf(false) }
    var filteredUsers by remember { mutableStateOf<List<UserEntityFirebase>>(emptyList()) }

    val allUsers by userViewModel.getAllUsers().collectAsState(initial = emptyList())

    LaunchedEffect(text) {
        val atIndex = text.lastIndexOf("@")

        if (atIndex != -1) {
            val query = text.substring(atIndex + 1)
                .takeWhile { it.isLetterOrDigit() || it == '_' }

            val afterMention = text.drop(atIndex + 1 + query.length)
            val mentionCompleted =
                afterMention.isNotEmpty() && afterMention.first().isWhitespace() ||
                        allUsers.any { it.username.equals(query, ignoreCase = true) }

            if (!mentionCompleted && (atIndex == 0 || text[atIndex - 1].isWhitespace())) {
                showUserDialog = true
                filteredUsers = if (query.isNotBlank()) {
                    allUsers.filter { it.username.contains(query, ignoreCase = true) }
                } else {
                    allUsers
                }
            } else {
                showUserDialog = false
            }
        } else {
            showUserDialog = false
        }
    }

    val annotatedText = buildAnnotatedString {
        val regex = Regex("@[A-Za-z0-9_]+")
        var lastIndex = 0
        for (match in regex.findAll(text)) {
            append(text.substring(lastIndex, match.range.first))
            withStyle(
                style = SpanStyle(color = Color(0xFF7DB2FF))
            ) {
                append(match.value)
            }
            lastIndex = match.range.last + 1
        }
        if (lastIndex < text.length) append(text.substring(lastIndex))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Icon(
            painter = painterResource(if (label == "Caption") R.drawable.caption else R.drawable.description),
            contentDescription = "Caption",
            modifier = Modifier
                .size(24.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawGradient()
        )

        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth()
                .height(44.dp)
                .border(
                    width = 0.5.dp,
                    brush = BrushPrimaryGradient,
                    shape = RoundedCornerShape(4.dp)
                )
                .clickable { showDescriptionDialog = true },
            contentAlignment = Alignment.CenterStart
        ) {
            val singleLineText = text
                .lines()
                .joinToString(" ") { it.trim() }
                .replace(Regex("\\s+"), " ")

            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = formatText(singleLineText, 38),
                color = GrayTextColor,
                maxLines = 1
            )
        }
    }

    if (showDescriptionDialog) {
        Dialog(
            onDismissRequest = { showDescriptionDialog = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PrimaryDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val focusRequester = remember { FocusRequester() }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (text.isNotBlank()) {
                                    showDiscardDialog = true
                                } else {
                                    showDescriptionDialog = false
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                            )
                        }

                        Icon(
                            painter = painterResource(if (label == "Caption") R.drawable.caption else R.drawable.description),
                            contentDescription = label,
                            modifier = Modifier
                                .graphicsLayer(alpha = 0.99f)
                                .drawGradient()
                        )

                        IconButton(onClick = { showDescriptionDialog = false }) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Done"
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    BasicTextField(
                        value = text,
                        onValueChange = { newText ->
                            // detect deletion
                            if (newText.length < text.length) {
                                val deletedIndex = newText.length

                                // find mention matches
                                val mentionRegex = Regex("@[A-Za-z0-9_]+")
                                val matches = mentionRegex.findAll(text).toList()

                                // check if deleted position was inside a mention
                                val matchToDelete = matches.find { deletedIndex in it.range }

                                if (matchToDelete != null) {
                                    // remove the entire mention
                                    val updatedText = buildString {
                                        append(text.substring(0, matchToDelete.range.first))
                                        append(text.substring(matchToDelete.range.last + 1))
                                    }
                                    onValueChange(updatedText.trimEnd())
                                } else {
                                    // normal delete or typing
                                    onValueChange(newText)
                                }
                            } else {
                                // typing forward or adding text
                                onValueChange(newText)
                            }
                        },
                        modifier = Modifier
                            .height(310.dp)
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showDescriptionDialog = true },
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        cursorBrush = SolidColor(Color.White),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SecondaryDark)
                                    .padding(vertical = 16.dp, horizontal = 8.dp)
                            ) {
                                if (text.isEmpty()) {
                                    Text(
                                        text = label,
                                        color = Color.Gray,
                                        modifier = Modifier.alpha(0.7f)
                                    )
                                }

                                innerTextField()

                                Text(text = annotatedText, color = Color.Transparent)
                            }
                        }
                    )
                }
            }
        }
    }

    if (showDiscardDialog) {
        ShowDiscardDialog(
            onValueChange = onValueChange,
            label = label,
            onShowDescriptionDialog = { showDescriptionDialog = it },
            onShowDiscardDialog = { showDiscardDialog = it }
        )
    }

    if (showUserDialog) {
        Dialog(onDismissRequest = { }) {
            Column(
                modifier = Modifier
                    .background(PrimaryDark)
                    .padding(8.dp)
                    .fillMaxWidth(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.mention),
                    contentDescription = "Mention",
                    modifier = Modifier
                        .graphicsLayer(alpha = 0.99f)
                        .drawWithCache {
                            onDrawWithContent {
                                drawContent()
                                drawRect(
                                    brush = BrushPrimaryGradient,
                                    blendMode = BlendMode.SrcAtop
                                )
                            }
                        }
                )

                Text(
                    text = "Mention a person",
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                )

                DrawLine()

                LazyColumn(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    if (filteredUsers.isEmpty()) {
                        item { CircularProgressIndicator() }
                    }

                    items(filteredUsers) { user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // replace @query with @username_here
                                    val atIndex = text.lastIndexOf("@")
                                    if (atIndex != -1) {
                                        val beforeAt = text.substring(0, atIndex)
                                        val mentionText = "@${user.username} "
                                        onValueChange(beforeAt + mentionText)
                                    }
                                    showUserDialog = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.mention),
                                contentDescription = "Mention",
                                modifier = Modifier
                                    .size(26.dp)
                                    .graphicsLayer(alpha = 0.99f)
                                    .drawWithCache {
                                        onDrawWithContent {
                                            drawContent()
                                            drawRect(
                                                brush = BrushPrimaryGradient,
                                                blendMode = BlendMode.SrcAtop
                                            )
                                        }
                                    }
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            AsyncImage(
                                model = user.avatarUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = user.username,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShowDiscardDialog(
    onValueChange: (String) -> Unit,
    label: String,
    onShowDescriptionDialog: (Boolean) -> Unit,
    onShowDiscardDialog: (Boolean) -> Unit
) {
    Dialog(
        onDismissRequest = {}
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(PrimaryDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.discard),
                    contentDescription = "Discard",
                    modifier = Modifier
                        .size(32.dp)
                        .graphicsLayer(alpha = 0.99f)
                        .drawGradient()
                )

                Text(
                    text = "You've already created a $label",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Are you sure you want to discard it?",
                    style = MaterialTheme.typography.titleSmall.copy(color = GrayTextColor)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(
                        onClick = {
                            onValueChange("")
                            onShowDiscardDialog(false)
                            onShowDescriptionDialog(false)
                        },
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.background(Color.Red, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Discard",
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(20.dp)
                        )

                        Text(text = "Discard")
                    }

                    TextButton(
                        onClick = { onShowDiscardDialog(false) },
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.background(BrushPrimaryGradient, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Cancel",
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(20.dp)
                        )

                        Text(text = "Cancel")
                    }
                }
            }
        }
    }
}

@Composable
fun SwitchButton(isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Make this post private",
            style = MaterialTheme.typography.titleSmall.copy(color = Color.White)
        )

        Box(
            modifier = Modifier
                .width(46.dp)
                .height(24.dp)
                .clip(CircleShape)
                .border(
                    width = 1.dp,
                    color = GrayTextColor,
                    shape = CircleShape
                )
                .background(
                    brush = Brush.horizontalGradient(
                        colors = if (isChecked) listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
                        else listOf(Color.Gray, Color.LightGray)
                    )
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier
                    .width(50.dp)
                    .height(24.dp)
                    .align(
                        if (isChecked) Alignment.CenterEnd else Alignment.CenterStart
                    ),
                thumbContent = {
                    Canvas(modifier = Modifier.size(18.dp)) {
                        drawCircle(
                            brush = if (isChecked) Brush.linearGradient(
                                listOf(
                                    Color.White,
                                    Color.White
                                )
                            ) else BrushPrimaryGradient,
                            radius = size.minDimension / 2
                        )
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Transparent,
                    uncheckedThumbColor = Color.Transparent,
                    checkedTrackColor = Color.Transparent,
                    uncheckedTrackColor = PrimaryDark,
                    uncheckedBorderColor = GrayTextColor
                )
            )
        }
    }
}

@Composable
fun CreatePostButton(
    mood: String,
    moodEmoji: String,
    hashtag: String,
    caption: String,
    description: String,
    type: String,
    uris: List<Uri?>,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    Column {
        Button(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
                .background(
                    brush = BrushPrimaryGradient,
                    shape = RoundedCornerShape(30.dp)
                ),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            onClick = {
                if (mood.isBlank() && moodEmoji.isBlank()) {
                    Toast.makeText(context, "Please select a mood", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (hashtag.isBlank()) {
                    Toast.makeText(context, "Please provide a hashtag", Toast.LENGTH_SHORT)
                        .show()
                    return@Button
                }
                if (caption.isBlank()) {
                    Toast.makeText(context, "Please provide a caption", Toast.LENGTH_SHORT)
                        .show()
                    return@Button
                }
                if (description.isBlank()) {
                    Toast.makeText(context, "Please provide a description", Toast.LENGTH_SHORT)
                        .show()
                    return@Button
                }

                val uploadIntent = Intent(context, UploadPostService::class.java).apply {
                    action = UploadPostService.ACTION_UPLOAD
                    putStringArrayListExtra(
                        UploadPostService.EXTRA_URIS,
                        ArrayList(uris.filterNotNull().map { it.toString() })
                    )
                    putExtra(UploadPostService.EXTRA_MOOD, mood)
                    putExtra(UploadPostService.EXTRA_MOOD_EMOJI, moodEmoji)
                    putExtra(UploadPostService.EXTRA_HASHTAG, hashtag)
                    putExtra(UploadPostService.EXTRA_CAPTION, caption)
                    putExtra(UploadPostService.EXTRA_DESCRIPTION, description)
                    putExtra(UploadPostService.EXTRA_TYPE, type)
                }

                context.startForegroundService(uploadIntent)
                Toast.makeText(context, "Uploading post...", Toast.LENGTH_SHORT).show()
                onBackClick()
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.post),
                contentDescription = "Post",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Post",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

fun getMoods(): List<Pair<String, String>> {
    return listOf(
        "😀" to "Happy",
        "😌" to "Calm",
        "🤗" to "Excited",
        "🙏" to "Grateful",
        "😬" to "Anxious",
        "😢" to "Sad",
        "😠" to "Angry",
        "😴" to "Sleepy",
        "🤔" to "Thoughtful",
        "😳" to "Embarrassed",
        "😇" to "Content",
        "🤩" to "Amazed",
        "🥰" to "Loved",
        "😭" to "Heartbroken",
        "😎" to "Confident",
        "😕" to "Confused",
        "😮" to "Surprised",
        "😒" to "Bored",
        "😤" to "Frustrated",
        "🤒" to "Sick",
        "🤪" to "Playful",
        "😞" to "Disappointed",
        "🥳" to "Cheerful",
        "🤯" to "Overwhelmed",
        "🥺" to "Hopeful",
        "😔" to "Lonely",
        "😱" to "Scared",
        "🤫" to "Secretive",
        "😐" to "Neutral",
        "🫠" to "Exhausted"
    )
}