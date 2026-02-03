package com.emc.moodmingle.utils.emojipicker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.ui.post.action.toastMessage
import com.emc.moodmingle.utils.emojipicker.utils.capitalizeWords

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmojiPicker() {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var isModalBottomSheetVisible by remember { mutableStateOf(false) }
    var selectedEmoji by remember { mutableStateOf("😃") }
    var searchText by remember { mutableStateOf("") }

    if (isModalBottomSheetVisible) {
        ModalBottomSheet(
            sheetState = sheetState,
            shape = RectangleShape,
            tonalElevation = 0.dp,
            onDismissRequest = {
                isModalBottomSheetVisible = false
                searchText = ""
            },
            dragHandle = null
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                EmojiPickerSheet(
                    onEmojiClick = { emoji ->
                        isModalBottomSheetVisible = false
                        selectedEmoji = emoji.character
                    },
                    onEmojiLongClick = { toastMessage(context, it.unicodeName.capitalizeWords()) },
                    searchText = searchText,
                    updateSearchText = { updatedSearchText -> searchText = updatedSearchText }
                )
            }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        EmojiPickerEmoji(
            emojiCharacter = selectedEmoji,
            onClick = { isModalBottomSheetVisible = true },
            fontSize = 56.sp
        )
    }
}