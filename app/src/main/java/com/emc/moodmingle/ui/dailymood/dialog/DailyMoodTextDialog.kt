package com.emc.moodmingle.ui.dailymood.dialog

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.ContentPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodEntity
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodText
import com.emc.moodmingle.ui.create.post.dialogs.CreatePostHashtagDialog
import com.emc.moodmingle.ui.create.post.dialogs.CreatePostMentionDialog
import com.emc.moodmingle.ui.create.post.pickers.CreatePostAlignPicker
import com.emc.moodmingle.ui.create.post.pickers.CreatePostColorPicker
import com.emc.moodmingle.ui.create.post.pickers.CreatePostFontPicker
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.color.toHex
import com.emc.moodmingle.utils.components.AnnotatedHashtag
import com.emc.moodmingle.utils.components.AnnotatedMention
import com.emc.moodmingle.utils.text.toColor
import com.emc.moodmingle.utils.text.toFontFamily
import com.emc.moodmingle.utils.text.toFontName
import com.emc.moodmingle.utils.text.toTextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyMoodTextDialog(
    dailyMood: DailyMoodEntity,
    onTextEdited: (DailyMoodEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var hashtag by remember { mutableStateOf(TextFieldValue("#", selection = TextRange(1))) }

    var selectedAction by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    BackHandler { if (selectedAction.isNotBlank()) selectedAction = "" else onDismiss() }

    Scaffold(
        containerColor = Color.Black,
        topBar = { TextDialogHeader(onDismiss, onTextEdited, dailyMood) },
        bottomBar = { TextDialogFooter(selectedAction) { selectedAction = it } },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActions(
                selectedAction,
                dailyMood,
                onActionSelected = { selectedAction = it },
                onTextEdited
            )
        }
    ) { paddingValues ->
        TextDialogContent(
            paddingValues,
            dailyMood,
            isFocused,
            focusRequester,
            onFocused = { isFocused = it },
            onTextChange = { onTextEdited(dailyMood.copy(text = dailyMood.text.copy(description = it))) }
        )
    }

    when (selectedAction) {
        "Text" -> focusRequester.requestFocus()
        "Hashtag" -> CreatePostHashtagDialog(
            hashtag = hashtag,
            onHashtagChange = {
                hashtag = it
                onTextEdited(dailyMood.copy(text = dailyMood.text.copy(hashtag = it.text)))
            },
            onDismiss = { selectedAction = "" }
        )

        "Mention" -> CreatePostMentionDialog(
            mentionUserIds = dailyMood.text.mentions,
            onMentionedUsers = { onTextEdited(dailyMood.copy(text = dailyMood.text.copy(mentions = it))) },
            onDismiss = { selectedAction = "" }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TextDialogHeader(
    onDismiss: () -> Unit,
    onTextEdited: (DailyMoodEntity) -> Unit,
    dailyMood: DailyMoodEntity
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            containerColor = PrimaryDark
        ),
        title = { Text(text = "Add Text", fontSize = 20.sp) },
        navigationIcon = {
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            TextButton(
                onClick = { onTextEdited(dailyMood); onDismiss() },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = SecondaryDark,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    top = ContentPadding.calculateTopPadding(),
                    end = 16.dp,
                    bottom = ContentPadding.calculateBottomPadding()
                ),
                enabled = dailyMood.text.description.isNotBlank() || dailyMood.text.hashtag.isNotBlank() || dailyMood.text.mentions.isNotEmpty()
            ) {
                Text(text = "Done", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun TextDialogFooter(selectedAction: String, onActionSelected: (String) -> Unit) {
    BottomAppBar(containerColor = PrimaryDark) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf(
                "Text" to R.drawable.text_colored,
                "Color" to R.drawable.color_wheel,
                "Font" to R.drawable.font_colored,
                "Align" to R.drawable.align_colored,
                "Hashtag" to R.drawable.hashtag_colored,
                "Mention" to R.drawable.mention_colored
            ).forEach { (label, icon) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clickable {
                                if (selectedAction == label) {
                                    onActionSelected("")
                                    return@clickable
                                }
                                onActionSelected(label)
                            }
                            .background(SecondaryDark, CircleShape)
                            .padding(12.dp)
                    ) {
                        Image(
                            painter = painterResource(icon),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Text(text = label, style = Typography.bodySmall.copy(color = Color.White))
                }
            }
        }
    }
}

@Composable
private fun FloatingActions(
    selectedAction: String,
    dailyMood: DailyMoodEntity,
    onActionSelected: (String) -> Unit,
    onTextEdited: (DailyMoodEntity) -> Unit
) {
    when (selectedAction) {
        "Font" -> {
            CreatePostFontPicker(selectedFont = dailyMood.text.font.toFontFamily()) {
                if (dailyMood.text.font == it.toFontName()) onActionSelected("")
                onTextEdited(dailyMood.copy(text = dailyMood.text.copy(font = it.toFontName())))
            }
        }

        "Color" -> {
            CreatePostColorPicker(selectedColor = dailyMood.text.color.toColor()) {
                onTextEdited(dailyMood.copy(text = dailyMood.text.copy(color = it.toHex())))
            }
        }

        "Align" -> {
            CreatePostAlignPicker(selectedAlign = dailyMood.text.align.toTextAlign()) {
                if (dailyMood.text.align == it.toString()) onActionSelected("")
                onTextEdited(dailyMood.copy(text = dailyMood.text.copy(align = it.toString())))
            }
        }
    }
}

@Composable
private fun DescriptionSection(
    dailyMoodText: DailyMoodText,
    isFocused: Boolean,
    focusRequester: FocusRequester,
    onFocused: (Boolean) -> Unit,
    onTextChange: (String) -> Unit
) {
    BasicTextField(
        value = dailyMoodText.description,
        onValueChange = { onTextChange(it) },
        cursorBrush = SolidColor(Color.White),
        textStyle = LocalTextStyle.current.copy(
            color = dailyMoodText.color.toColor(),
            fontSize = 20.sp,
            textAlign = if (dailyMoodText.align.toTextAlign() == TextAlign.Unspecified && dailyMoodText.description.isEmpty()) TextAlign.Center else dailyMoodText.align.toTextAlign(),
            fontFamily = dailyMoodText.font.toFontFamily()
        ),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (dailyMoodText.description.isEmpty() && !isFocused) {
                    Text(text = "Tap to enter text...", fontSize = 16.sp, color = GrayTextColor)
                }

                innerTextField()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged { onFocused(it.isFocused) }

    )
}

@Composable
private fun TextDialogContent(
    paddingValues: PaddingValues,
    dailyMood: DailyMoodEntity,
    isFocused: Boolean,
    focusRequester: FocusRequester,
    onFocused: (Boolean) -> Unit,
    onTextChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnnotatedMention(mentions = dailyMood.text.mentions)
            AnnotatedHashtag(hashtag = dailyMood.text.hashtag)
            DescriptionSection(
                dailyMoodText = dailyMood.text,
                isFocused,
                focusRequester,
                onFocused,
                onTextChange
            )
        }
    }
}