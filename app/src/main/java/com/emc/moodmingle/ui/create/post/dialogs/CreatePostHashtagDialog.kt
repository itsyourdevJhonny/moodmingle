package com.emc.moodmingle.ui.create.post.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.ui.create.post.hashtag.HashtagChips
import com.emc.moodmingle.ui.create.post.hashtag.HashtagCounterAndRemoveIcon
import com.emc.moodmingle.ui.create.post.hashtag.HashtagInputField
import com.emc.moodmingle.ui.create.post.hashtag.extractHashtags
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.utils.components.BackIcon

@Composable
fun CreatePostHashtagDialog(
    hashtag: TextFieldValue,
    onHashtagChange: (TextFieldValue) -> Unit,
    onDismiss: () -> Unit
) {
    Scaffold(
        containerColor = Color.Black,
        topBar = { HashtagDialogHeader(hashtag, onHashtagChange, onDismiss) },
    ) { paddingValues ->
        HashtagDialogContent(paddingValues, hashtag, onHashtagChange)
    }
}

@Composable
private fun HashtagDialogContent(
    paddingValues: PaddingValues,
    hashtag: TextFieldValue,
    onHashtagChange: (TextFieldValue) -> Unit
) {
    val tags = extractHashtags(hashtag.text)

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 8.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HashtagCounterAndRemoveIcon(tags, onHashtagChange)
        HashtagChips(hashtags = tags)
        HashtagInputField(hashtag, onHashtagChange)
    }
}

@Composable
fun HashtagDialogHeader(
    hashtag: TextFieldValue,
    onHashtagChange: (TextFieldValue) -> Unit,
    onDismiss: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BackIcon(onClick = onDismiss)
            Text(text = "Create Hashtags", color = Color.White)
        }

        TextButton(
            onClick = {
                onHashtagChange(hashtag)
                onDismiss()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryDark,
                contentColor = Color.White
            ),
            enabled = hashtag.text.isNotEmpty() && hashtag.text != "#"
        ) {
            Text(text = "Okay", fontWeight = FontWeight.Bold)
        }
    }
}