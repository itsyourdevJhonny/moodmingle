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
import androidx.compose.runtime.collectAsState
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodText
import com.emc.moodmingle.ui.create.post.dialogs.CreatePostHashtagDialog
import com.emc.moodmingle.ui.create.post.dialogs.CreatePostMentionDialog
import com.emc.moodmingle.ui.create.post.hashtag.extractHashtags
import com.emc.moodmingle.ui.create.post.pickers.CreatePostAlignPicker
import com.emc.moodmingle.ui.create.post.pickers.CreatePostColorPicker
import com.emc.moodmingle.ui.create.post.pickers.CreatePostFontPicker
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.color.toHex
import com.emc.moodmingle.utils.components.ExpandableAnnotatedText
import com.emc.moodmingle.utils.text.toColor
import com.emc.moodmingle.utils.text.toFontFamily
import com.emc.moodmingle.utils.text.toFontName
import com.emc.moodmingle.utils.text.toTextAlign
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyMoodTextDialog(onDismiss: () -> Unit, onTextCreated: (DailyMoodText) -> Unit) {
    var dailyMoodText by remember { mutableStateOf(DailyMoodText()) }
    var hashtag by remember { mutableStateOf(TextFieldValue("#", selection = TextRange(1))) }

    var selectedAction by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    BackHandler { onDismiss() }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    containerColor = PrimaryDark
                ),
                title = { Text(text = "Add Text", fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { onDismiss() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            onTextCreated(dailyMoodText)
                            onDismiss()
                        },
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
                        enabled = dailyMoodText.text.isNotBlank() || dailyMoodText.hashtag.isNotBlank() || dailyMoodText.mentions.isNotEmpty()
                    ) {
                        Text(text = "Done", fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        bottomBar = {
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
                                            selectedAction = ""
                                            return@clickable
                                        }

                                        selectedAction = label
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

                            Text(
                                text = label,
                                style = Typography.bodySmall.copy(color = Color.White)
                            )
                        }
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            when (selectedAction) {
                "Font" -> {
                    CreatePostFontPicker(selectedFont = dailyMoodText.font.toFontFamily()) {
                        if (dailyMoodText.font == it.toFontName()) selectedAction = ""
                        dailyMoodText = dailyMoodText.copy(font = it.toFontName())
                    }
                }

                "Color" -> {
                    CreatePostColorPicker(selectedColor = dailyMoodText.color.toColor()) {
                        if (dailyMoodText.color == it.toHex()) selectedAction = ""
                        dailyMoodText = dailyMoodText.copy(color = it.toHex())
                    }
                }

                "Align" -> {
                    CreatePostAlignPicker(selectedAlign = dailyMoodText.align.toTextAlign()) {
                        if (dailyMoodText.align == it.toString()) selectedAction = ""
                        dailyMoodText = dailyMoodText.copy(align = it.toString())
                    }
                }
            }
        }
    ) { paddingValues ->
        TextDialogContent(
            paddingValues,
            dailyMoodText,
            isFocused,
            focusRequester,
            onFocused = { isFocused = it },
            onTextChange = { dailyMoodText = dailyMoodText.copy(text = it) }
        )
    }

    when (selectedAction) {
        "Text" -> focusRequester.requestFocus()
        "Hashtag" -> CreatePostHashtagDialog(
            hashtag = hashtag,
            onHashtagChange = {
                hashtag = it
                dailyMoodText = dailyMoodText.copy(hashtag = it.text)
            },
            onDismiss = { selectedAction = "" }
        )

        "Mention" -> CreatePostMentionDialog(
            mentionUserIds = dailyMoodText.mentions,
            onMentionedUsers = { dailyMoodText = dailyMoodText.copy(mentions = it) },
            onDismiss = { selectedAction = "" }
        )
    }

}

@Composable
fun TextSection(
    dailyMoodText: DailyMoodText,
    isFocused: Boolean,
    focusRequester: FocusRequester,
    onFocused: (Boolean) -> Unit,
    onTextChange: (String) -> Unit
) {
    BasicTextField(
        value = dailyMoodText.text,
        onValueChange = { onTextChange(it) },
        cursorBrush = SolidColor(Color.White),
        textStyle = LocalTextStyle.current.copy(
            color = dailyMoodText.color.toColor(),
            fontSize = 20.sp,
            textAlign = if (dailyMoodText.align.toTextAlign() == TextAlign.Unspecified && dailyMoodText.text.isEmpty()) TextAlign.Center else dailyMoodText.align.toTextAlign(),
            fontFamily = dailyMoodText.font.toFontFamily()
        ),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (dailyMoodText.text.isEmpty() && !isFocused) {
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
fun TextDialogContent(
    paddingValues: PaddingValues,
    dailyMoodText: DailyMoodText,
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
            if (dailyMoodText.mentions.isNotEmpty()) {
                val userViewModel = hiltViewModel<FirebaseUserViewModel>()

                val mentionUsernames = dailyMoodText.mentions.map { userId ->
                    val user by remember(userId) {
                        userViewModel.getUserById(userId)
                    }.collectAsState(initial = null)

                    user?.username ?: ""
                }

                ExpandableAnnotatedText(
                    fullText = mentionUsernames.joinToString(", ") { "@$it" },
                    minLines = 1
                )
            }

            TextSection(dailyMoodText, isFocused, focusRequester, onFocused, onTextChange)

            if (dailyMoodText.hashtag.isNotBlank()) {
                val hashtags = extractHashtags(dailyMoodText.hashtag)

                ExpandableAnnotatedText(
                    fullText = hashtags.joinToString(" ") { "#${it.replace(" ", "")}" },
                    style = Typography.bodyLarge.copy(lineHeight = 20.sp),
                    minLines = 1
                )
            }
        }
    }
}