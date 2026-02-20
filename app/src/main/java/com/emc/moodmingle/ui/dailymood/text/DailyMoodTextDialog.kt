package com.emc.moodmingle.ui.dailymood.text

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.ContentPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedIconButton
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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.post.dailymood.DailyMoodEntity
import com.emc.moodmingle.domain.remote.model.post.dailymood.text.DailyMoodText
import com.emc.moodmingle.domain.remote.model.post.dailymood.text.TextStyle
import com.emc.moodmingle.ui.create.post.dialogs.CreatePostHashtagDialog
import com.emc.moodmingle.ui.create.post.dialogs.CreatePostMentionDialog
import com.emc.moodmingle.ui.create.post.pickers.CreatePostAlignPicker
import com.emc.moodmingle.ui.create.post.pickers.CreatePostColorPicker
import com.emc.moodmingle.ui.create.post.pickers.CreatePostFontPicker
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
fun DailyMoodEditText(
    mood: DailyMoodEntity,
    onEdited: (DailyMoodEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var hashtag by remember { mutableStateOf(TextFieldValue("#", selection = TextRange(1))) }

    var selectedAction by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    BackHandler { if (selectedAction.isNotBlank()) selectedAction = "" else onDismiss() }

    Scaffold(
        containerColor = Color.Black,
        topBar = { Header(onDismiss, onEdited, mood) },
        bottomBar = { Footer(selectedAction) { selectedAction = it } },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActions(
                selectedAction,
                mood,
                onActionSelected = { selectedAction = it },
                onEdited
            )
        }
    ) { paddingValues ->
        TextDialogContent(
            paddingValues,
            mood,
            isFocused,
            focusRequester,
            onFocused = { isFocused = it },
            onTextChange = { onEdited(mood.copy(text = mood.text.copy(description = it))) }
        )
    }

    when (selectedAction) {
        "Text" -> focusRequester.requestFocus()
        "Hashtag" -> CreatePostHashtagDialog(
            hashtag = hashtag,
            onHashtagChange = {
                hashtag = it
                onEdited(mood.copy(text = mood.text.copy(hashtag = it.text)))
            },
            onDismiss = { selectedAction = "" }
        )

        "Mention" -> CreatePostMentionDialog(
            mentionUserIds = mood.text.mentions,
            onMentionedUsers = {
                onEdited(
                    mood.copy(
                        text = mood.text.copy(
                            mentions = it
                        )
                    )
                )
            },
            onDismiss = { selectedAction = "" }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Header(
    onDismiss: () -> Unit,
    onEdited: (DailyMoodEntity) -> Unit,
    mood: DailyMoodEntity
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
                onClick = { onEdited(mood); onDismiss() },
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
                enabled = mood.text.description.isNotBlank() || mood.text.hashtag.isNotBlank() || mood.text.mentions.isNotEmpty()
            ) {
                Text(text = "Done", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun Footer(selectedAction: String, onActionSelected: (String) -> Unit) {
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
    mood: DailyMoodEntity,
    onActionSelected: (String) -> Unit,
    onEdited: (DailyMoodEntity) -> Unit
) {
    val text = mood.text
    val labelColor = if (text.color.toColor().luminance() < 0.5f) Color.White else Color.Black

    var isTextAnimate by remember { mutableStateOf(false) }
    var isTextEffect by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (selectedAction) {
            "Font" -> {
                CreatePostFontPicker(selectedFont = mood.text.font.toFontFamily()) {
                    if (mood.text.font == it.toFontName()) onActionSelected("")
                    onEdited(mood.copy(text = mood.text.copy(font = it.toFontName())))
                }
            }

            "Color" -> {
                CreatePostColorPicker(selectedColor = mood.text.color.toColor()) {
                    onEdited(mood.copy(text = mood.text.copy(color = it.toHex())))
                }
            }

            "Align" -> {
                CreatePostAlignPicker(selectedAlign = mood.text.align.toTextAlign()) {
                    if (mood.text.align == it.toString()) onActionSelected("")
                    onEdited(mood.copy(text = mood.text.copy(align = it.toString())))
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AssistChip(
                onClick = {
                    onEdited(
                        mood.copy(
                            text = text.copy(
                                style = when (text.style) {
                                    TextStyle.NORMAL -> TextStyle.WITH_BACKGROUND
                                    TextStyle.WITH_BACKGROUND -> TextStyle.WITHOUT_BACKGROUND
                                    TextStyle.WITHOUT_BACKGROUND -> TextStyle.NORMAL
                                }
                            )
                        )
                    )
                },
                label = { Text(text = "A", fontSize = 24.sp) },
                leadingIcon = {},
                border = if (text.style == TextStyle.NORMAL) BorderStroke(
                    width = 1.dp,
                    color = text.color.toColor()
                ) else null,
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = when (text.style) {
                        TextStyle.NORMAL -> Color.Transparent
                        TextStyle.WITH_BACKGROUND -> text.color.toColor()
                        TextStyle.WITHOUT_BACKGROUND -> text.color.toColor()
                            .copy(alpha = 0.3f)
                    },
                    labelColor = when (text.style) {
                        TextStyle.NORMAL -> Color.White
                        TextStyle.WITH_BACKGROUND -> labelColor
                        TextStyle.WITHOUT_BACKGROUND -> Color.White
                    }
                )
            )

            ToggleOutlinedIconButton(
                iconRes = R.drawable.text_animate,
                isActive = isTextAnimate,
                onClick = { isTextEffect = false; isTextAnimate = !isTextAnimate }
            )

            ToggleOutlinedIconButton(
                iconRes = R.drawable.text_effect,
                isActive = isTextEffect,
                onClick = { isTextAnimate = false; isTextEffect = !isTextEffect }
            )
        }
    }
}

@Composable
private fun ToggleOutlinedIconButton(iconRes: Int, isActive: Boolean, onClick: () -> Unit) {
    OutlinedIconButton(
        onClick = onClick,
        colors = IconButtonDefaults.outlinedIconButtonColors(
            containerColor = if (isActive) SecondaryDark else Color.Transparent,
            contentColor = Color.White
        ),
        border = BorderStroke(1.dp, if (isActive) SecondaryDark else Color.White)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun ColumnScope.DescriptionSection(
    text: DailyMoodText,
    isFocused: Boolean,
    focusRequester: FocusRequester,
    onFocused: (Boolean) -> Unit,
    onTextChange: (String) -> Unit
) {
    val color = if (text.color.toColor().luminance() < 0.5f) Color.White else Color.Black

    BasicTextField(
        value = text.description,
        onValueChange = { onTextChange(it) },
        cursorBrush = SolidColor(
            if (text.color.toColor() == Color.White) {
                if (text.style == TextStyle.NORMAL) Color.White else Color.Black
            } else {
                if (text.style == TextStyle.NORMAL) text.color.toColor() else color
            }
        ),
        textStyle = LocalTextStyle.current.copy(
            color = when (text.style) {
                TextStyle.NORMAL -> text.color.toColor()
                TextStyle.WITH_BACKGROUND -> color
                TextStyle.WITHOUT_BACKGROUND -> Color.White
            },
            fontSize = 20.sp,
            textAlign = if (text.align.toTextAlign() == TextAlign.Unspecified && text.description.isEmpty()) TextAlign.Center else text.align.toTextAlign(),
            fontFamily = text.font.toFontFamily(),
        ),
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.Center) {
                if (text.description.isEmpty() && !isFocused) {
                    Text(
                        text = "Tap to enter text...",
                        fontSize = 16.sp,
                        color = if (text.style == TextStyle.NORMAL) Color.White else color
                    )
                }

                Box(modifier = Modifier.padding(8.dp)) { innerTextField() }
            }
        },
        modifier = Modifier
            .focusRequester(focusRequester)
            .onFocusChanged { onFocused(it.isFocused) }
            .align(Alignment.CenterHorizontally)
            .background(
                color = when (text.style) {
                    TextStyle.NORMAL -> Color.Transparent
                    TextStyle.WITH_BACKGROUND -> text.color.toColor()
                    TextStyle.WITHOUT_BACKGROUND -> text.color.toColor().copy(alpha = 0.3f)
                },
                shape = RoundedCornerShape(8.dp)
            )

    )
}

@Composable
private fun TextDialogContent(
    paddingValues: PaddingValues,
    mood: DailyMoodEntity,
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
            AnnotatedMention(mentions = mood.text.mentions)
            AnnotatedHashtag(hashtag = mood.text.hashtag)
            DescriptionSection(
                text = mood.text,
                isFocused,
                focusRequester,
                onFocused,
                onTextChange
            )
        }
    }
}