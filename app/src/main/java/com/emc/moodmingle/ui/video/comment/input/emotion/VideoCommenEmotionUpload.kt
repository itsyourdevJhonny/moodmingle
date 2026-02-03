package com.emc.moodmingle.ui.video.comment.input.emotion

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.screens.getMoods
import com.emc.moodmingle.ui.theme.PurpleDark
import com.emc.moodmingle.ui.theme.PurplePrimary
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.roundedGrayBorder

@Composable
fun VideoCommentEmotionUpload(emotion: String, onSelectedEmotion: (Pair<String, String>) -> Unit) {
    var showEmotions by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(top = 8.dp)
            .background(SecondaryDark, RoundedCornerShape(8.dp))
            .roundedGrayBorder(8.dp)
            .clickable { showEmotions = true }
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.feelings_colored),
                contentDescription = "Emotion Tag",
                modifier = Modifier.size(20.dp)
            )

            Text(text = "Emotion", style = Typography.bodySmall.copy(color = Color.White))
        }
    }

    if (showEmotions) {
        EmotionsDialog(emotion, onDismiss = { showEmotions = false }, onSelectedEmotion)
    }
}

@Composable
private fun EmotionsDialog(
    emotion: String,
    onDismiss: () -> Unit,
    onSelectedEmotion: (Pair<String, String>) -> Unit
) {
    val emotions = getMoods()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            items(emotions) { (emoji, text) ->
                val isSelected = text == emotion

                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .padding(4.dp)
                        .background(SecondaryDark, RoundedCornerShape(8.dp))
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) PurpleDark else PurplePrimary.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            if (isSelected) onSelectedEmotion("" to "") else onSelectedEmotion(emoji to text)
                            onDismiss()
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
}