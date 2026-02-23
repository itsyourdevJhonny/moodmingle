package com.emc.moodmingle.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.post.remix.Mood
import com.emc.moodmingle.domain.remote.model.post.remix.RemixEntity
import com.emc.moodmingle.domain.remote.model.post.remix.RemixEntityRemix
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.domain.remote.model.video.VideoComment
import com.emc.moodmingle.domain.remote.viewmodel.post.remix.RemixViewModel
import com.emc.moodmingle.domain.remote.viewmodel.video.VideoCommentViewModel
import com.emc.moodmingle.domain.local.model.post.formatTimeAgo
import com.emc.moodmingle.ui.remix.RemixSecondaryActions
import com.emc.moodmingle.ui.remix.RemixColorPickerDialog
import com.emc.moodmingle.ui.remix.RemixFontPickerDialog
import com.emc.moodmingle.ui.remix.RemixInformationActions
import com.emc.moodmingle.ui.remix.RemixPostInformationSwitchButtons
import com.emc.moodmingle.ui.remix.RemixTextAlignPickerDialog
import com.emc.moodmingle.ui.remix.RemixUseCommentSwitchButton
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.TertiaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.color.toHex
import com.emc.moodmingle.utils.components.BackIcon
import com.emc.moodmingle.utils.font.FontUtils
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.utils.modifier.gradientCircleBorder
import com.emc.moodmingle.utils.modifier.grayCircleBorder
import com.emc.moodmingle.utils.modifier.roundedGrayBorder
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel
import kotlinx.coroutines.flow.first

@Composable
fun RemixScreen(entityId: String, type: String, onBack: () -> Unit) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val videoCommentViewModel = hiltViewModel<VideoCommentViewModel>()
    val remixViewModel = hiltViewModel<RemixViewModel>()

    val currentUser by userViewModel.loggedUser
    var entity by remember { mutableStateOf<Any?>(null) }

    LaunchedEffect(entityId) {
        when (type) {
            "COMMENT" -> entity = videoCommentViewModel.getCommentById(entityId)
            "POST" -> entity = remixViewModel.getRemixById(entityId).first()
        }
    }

    val inspirer by remember(entity) {
        userViewModel.getUserById(
            when (type) {
                "COMMENT" -> (entity as VideoComment?)?.commenterId ?: ""
                else -> (entity as RemixEntity?)?.userId ?: ""
            }
        )
    }.collectAsState(initial = null)

    var useCommentMessage by remember { mutableStateOf(false) }
    var isHidden by remember { mutableStateOf(false) }

    var hashtag by remember { mutableStateOf("") }
    var caption by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var showColorPicker by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf(SecondaryDark) }

    var showFontPicker by remember { mutableStateOf(false) }
    var currentFont by remember { mutableStateOf(FontFamily.Default as FontFamily) }

    var showAlignPicker by remember { mutableStateOf(false) }
    var currentAlign by remember { mutableStateOf(TextAlign.Unspecified) }

    var selectedMood by remember { mutableStateOf(Mood()) }

    var useHashtag by remember { mutableStateOf(false) }
    var useCaption by remember { mutableStateOf(false) }
    var useDescription by remember { mutableStateOf(false) }

    Scaffold(topBar = {
        Header(
            hashtag,
            caption,
            description,
            currentUser,
            inspirer,
            selectedColor,
            currentFont,
            currentAlign,
            type,
            entity,
            selectedMood,
            onBack
        )
    }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(PrimaryDark),
            contentAlignment = Alignment.Center
        ) {
            when (type) {
                "COMMENT" -> {
                    RemixUseCommentSwitchButton(
                        entity as VideoComment,
                        useCommentMessage,
                        onUseCommentMessage = { useCommentMessage = it },
                        onDescriptionChange = { description = it }
                    )
                }

                "POST" -> {
                    RemixPostInformationSwitchButtons(
                        entity,
                        useHashtag,
                        useCaption,
                        useDescription,
                        onUseHashtag = { useHashtag = it },
                        onUseCaption = { useCaption = it },
                        onUseDescription = { useDescription = it },
                        onHashtagChanged = { hashtag = it },
                        onCaptionChanged = { caption = it },
                        onDescriptionChanged = { description = it }
                    )
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PreviewText()

                Box(
                    modifier = Modifier
                        .background(
                            if (selectedColor == Color.White) SecondaryDark else selectedColor,
                            RoundedCornerShape(16.dp)
                        )
                ) {
                    val textColor =
                        if (selectedColor.luminance() < 0.5f) Color.White else Color.Black

                    Column(modifier = Modifier.padding(8.dp)) {
                        CurrentUserAvatarAndUsername(
                            currentUser,
                            textColor,
                            selectedColor,
                            selectedMood
                        )

                        PreviewCommentInformation(
                            hashtag,
                            caption,
                            description,
                            textColor,
                            currentFont,
                            currentAlign
                        )
                    }

                    InspiredBy(inspirer, selectedColor, textColor)
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .zIndex(1f)
            ) {
                RemixInformationActions(
                    isHidden,
                    hashtag,
                    caption,
                    description,
                    selectedMood,
                    onHidden = { isHidden = it },
                    onHashtagChange = { hashtag = it },
                    onCaptionChange = { caption = it },
                    onDescriptionChange = { description = it },
                    onSelectedMood = { selectedMood = it }
                )

                UnHideAction(isHidden, onHidden = { isHidden = it })
            }

            RemixSecondaryActions(
                isHidden,
                onShowColorPicker = { showColorPicker = it },
                onShowFontPicker = { showFontPicker = it },
                onShowAlignPicker = { showAlignPicker = it }
            )
        }

        if (showColorPicker) {
            RemixColorPickerDialog(
                selectedColor,
                onColorSelected = { selectedColor = it },
                onDismiss = { showColorPicker = false }
            )
        }

        if (showFontPicker) {
            RemixFontPickerDialog(
                hashtag,
                caption,
                description,
                currentFont,
                onDismiss = { showFontPicker = false },
                onFontSelected = { currentFont = it }
            )
        }

        if (showAlignPicker) {
            RemixTextAlignPickerDialog(
                hashtag,
                caption,
                description,
                currentAlign,
                onAlignSelected = { currentAlign = it },
                onDismiss = { showAlignPicker = false }
            )
        }
    }
}

@Composable
private fun PreviewText() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.view),
            contentDescription = "Preview",
            tint = Color.White,
            modifier = Modifier
                .size(20.dp)
                .drawGradient()
        )

        Text(
            text = "Preview",
            style = Typography.titleMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun CurrentUserAvatarAndUsername(
    currentUser: UserEntityFirebase?,
    textColor: Color,
    selectedColor: Color,
    selectedMood: Mood
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            AsyncImage(
                model = currentUser?.avatarUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .gradientCircleBorder(),
                contentScale = ContentScale.Crop
            )

            Column {
                Text(
                    text = currentUser?.username ?: "",
                    style = Typography.bodyLarge.copy(
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = formatTimeAgo(System.currentTimeMillis() - 5000),
                    style = Typography.bodySmall.copy(color = textColor)
                )
            }
        }

        if (selectedMood.description.isNotEmpty()) {
            Box(
                modifier = Modifier.background(textColor, RoundedCornerShape(12.dp))
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = selectedMood.emoji,
                        style = Typography.bodyLarge.copy(color = Color.White)
                    )

                    Text(
                        text = selectedMood.description,
                        style = Typography.labelSmall.copy(
                            color = selectedColor,
                            fontWeight = FontWeight.Black
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun PreviewCommentInformation(
    hashtag: String,
    caption: String,
    description: String,
    textColor: Color,
    currentFont: FontFamily,
    currentAlign: TextAlign
) {
    listOf(
        hashtag to R.drawable.hashtag,
        caption to R.drawable.caption,
        description to R.drawable.description
    ).forEach { (value, icon) ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = "Action",
                tint = textColor,
                modifier = Modifier.size(18.dp)
            )

            Text(
                text = value.ifEmpty { "..." },
                style = when (icon) {
                    R.drawable.hashtag -> Typography.titleMedium.copy(fontWeight = FontWeight.Black)
                    R.drawable.caption -> Typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    else -> Typography.bodyMedium
                },
                fontFamily = currentFont,
                textAlign = currentAlign,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = textColor,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (icon != R.drawable.description) {
            HorizontalDivider(
                thickness = 0.5.dp,
                modifier = Modifier.padding(vertical = 10.dp),
                color = textColor
            )
        }
    }
}

@Composable
fun BoxScope.InspiredBy(
    commenter: UserEntityFirebase?,
    selectedColor: Color,
    textColor: Color
) {
    val backgroundColor = if (selectedColor == Color.White) TertiaryDark else selectedColor

    Column(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .offset(y = (-96).dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .background(backgroundColor, RoundedCornerShape(8.dp))
                    .roundedGrayBorder(8.dp)
            ) {
                Text(
                    text = "Inspired by:",
                    style = Typography.bodySmall.copy(color = textColor),
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(backgroundColor, CircleShape)
                    .align(Alignment.CenterHorizontally)
            )
        }

        Box(modifier = Modifier.background(backgroundColor, CircleShape)) {
            Text(
                text = "${commenter?.username}",
                style = Typography.bodyMedium.copy(
                    color = textColor,
                    fontWeight = FontWeight.Black
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .widthIn(max = 150.dp)
            )
        }

        Box(
            modifier = Modifier
                .size(24.dp)
                .offset(x = (-32).dp, y = (-4).dp)
                .background(backgroundColor, CircleShape)
                .align(Alignment.End)
        )

        Box(
            modifier = Modifier
                .size(16.dp)
                .offset(x = (-24).dp, y = (-10).dp)
                .background(backgroundColor, CircleShape)
                .align(Alignment.End)
        )
    }
}

@Composable
private fun UnHideAction(isHidden: Boolean, onHidden: (Boolean) -> Unit) {
    AnimatedVisibility(
        visible = isHidden,
        enter = expandHorizontally(initialWidth = { maxWidth -> maxWidth / 100 }),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(SecondaryDark, CircleShape)
                .grayCircleBorder()
                .clickable { onHidden(false) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.visibility_on),
                contentDescription = "Action",
                modifier = Modifier
                    .size(20.dp)
                    .drawGradient()
            )
        }
    }
}

@Composable
private fun Header(
    hashtag: String,
    caption: String,
    description: String,
    currentUser: UserEntityFirebase?,
    inspirer: UserEntityFirebase?,
    selectedColor: Color,
    currentFont: FontFamily,
    currentAlign: TextAlign,
    type: String,
    entity: Any?,
    selectedMood: Mood,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val remixViewModel = hiltViewModel<RemixViewModel>()

    fun toastMessage(text: String) {
        Toast.makeText(context, "Please provide a $text", Toast.LENGTH_SHORT).show()
    }

    Row(
        modifier = Modifier
            .padding(top = 38.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BackIcon(onClick = onBack)

            Text(
                text = "Remix ${if (type == "COMMENT") "Comment" else "Post"}",
                style = Typography.titleMedium.copy(color = Color.White)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(BrushPrimaryGradient, CircleShape)
                    .clickable {
                        if (selectedMood.description.isEmpty()) {
                            toastMessage("mood")
                            return@clickable
                        }

                        if (hashtag.isEmpty()) {
                            toastMessage("hashtag")
                            return@clickable
                        }

                        if (caption.isEmpty()) {
                            toastMessage("caption")
                            return@clickable
                        }

                        if (description.isEmpty()) {
                            toastMessage("description")
                            return@clickable
                        }

                        val newRemix = RemixEntity(
                            userId = currentUser?.uid.orEmpty(),
                            inspirerId = inspirer?.uid.orEmpty(),
                            hashtag = hashtag,
                            caption = caption,
                            description = description,
                            mood = selectedMood,
                            color = selectedColor.toHex(),
                            fontStyle = FontUtils.getFontName(currentFont),
                            textAlignment = currentAlign.toString()
                        )

                        remixViewModel.insertRemix(newRemix)

                        if (type == "POST") {
                            val remix = entity as RemixEntity?

                            if (remix != null) {
                                val updatedRemix = remix.copy(
                                    remixes = remix.remixes + RemixEntityRemix(currentUser?.uid.orEmpty())
                                )

                                remixViewModel.updateRemix(updatedRemix)
                            }
                        }

                        Toast.makeText(context, "Remix posted!", Toast.LENGTH_SHORT).show()

                        onBack()
                    }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(4.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.post),
                        contentDescription = "Post",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )

                    AsyncImage(
                        model = currentUser?.avatarUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .gradientCircleBorder(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.White,
                modifier = Modifier
                    .size(28.dp)
                    .clickable {}
            )
        }
    }
}