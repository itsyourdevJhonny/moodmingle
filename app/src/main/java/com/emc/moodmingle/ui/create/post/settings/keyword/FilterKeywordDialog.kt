package com.emc.moodmingle.ui.create.post.settings.keyword

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.emc.moodmingle.ui.create.post.CreatePostDialogHeader
import com.emc.moodmingle.ui.post.action.toastMessage

@Composable
fun FilterKeywordDialog(
    filteredKeywords: List<String>,
    onKeywordsFiltered: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val keywords = remember { mutableStateListOf<String>() }

    if (filteredKeywords.isNotEmpty()) keywords.addAll(filteredKeywords)

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            CreatePostDialogHeader(
                label = "Filter Keywords",
                onBack = {
                    if (filteredKeywords.size != keywords.size) {
                        toastMessage(context, "Settings Saved")
                    }

                    if (keywords.isNotEmpty()) onKeywordsFiltered(keywords.toList())
                    onDismiss()
                }
            )
        }
    ) { paddingValues ->
        FilterKeywordDialogContent(paddingValues, keywords)
    }
}