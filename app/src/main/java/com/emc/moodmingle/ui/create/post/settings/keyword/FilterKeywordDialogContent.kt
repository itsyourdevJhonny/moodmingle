package com.emc.moodmingle.ui.create.post.settings.keyword

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.post.action.toastMessage
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.MentionTextColor
import com.emc.moodmingle.ui.theme.Typography

@Composable
fun FilterKeywordDialogContent(paddingValues: PaddingValues, keywords: SnapshotStateList<String>) {
    var keyword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = "Block words containing specific keywords to maintain a safe and respectful environment. " +
                    "Add unwanted words to your filter list to automatically restrict comments, support or triggering messages.",
            style = Typography.bodyMedium.copy(color = GrayTextColor, textAlign = TextAlign.Center)
        )

        HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(top = 16.dp))

        KeywordField(keywords, keyword) { keyword = it }

        HorizontalDivider(thickness = 0.5.dp)

        CounterIndicator(keywords)

        if (keywords.isNotEmpty()) {
            KeywordChips(keywords)
        }
    }
}

@Composable
private fun KeywordField(
    keywords: SnapshotStateList<String>,
    keyword: String,
    onKeywordChange: (String) -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val addAction = {
        if (keyword.isNotBlank() && keyword !in keywords) {
            if (keyword.all { it.isLetter() }) {
                keywords.add(keyword)
                onKeywordChange("")
            } else {
                toastMessage(context, "Please enter a letters only")
            }
        }
    }

    OutlinedTextField(
        value = keyword,
        onValueChange = onKeywordChange,
        singleLine = true,
        placeholder = { Text(text = "Enter keyword..") },
        trailingIcon = { KeywordFieldTrailingIcon(keyword, context, addAction) },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.Transparent,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color.Transparent,
            focusedTextColor = Color.White,
            cursorColor = Color.White
        ),
        keyboardOptions = KeyboardOptions(imeAction = if (keyword.isBlank()) ImeAction.Go else ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = { addAction() },
            onGo = { focusManager.clearFocus() }
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun KeywordFieldTrailingIcon(keyword: String, context: Context, addAction: () -> Unit) {
    AnimatedVisibility(visible = keyword.isNotBlank()) {
        Row(
            modifier = Modifier
                .clickable {
                    if (keyword.isBlank()) toastMessage(context, "Please enter a keyword")
                    else addAction()
                }
                .background(BrushPrimaryGradient, CircleShape)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            Text(text = " Add", color = Color.White)
        }
    }
}

@Composable
private fun CounterIndicator(keywords: SnapshotStateList<String>) {
    val size = keywords.size
    val prefix = if (size == 0) "No" else size.toString()
    val suffix = if (size > 1) "s" else ""

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .animateContentSize()
    ) {
        Text(
            modifier = Modifier
                .animateContentSize(),
            text = "$prefix Keyword$suffix Filtered",
            style = Typography.titleSmall
        )

        AnimatedVisibility(visible = size > 1) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { keywords.clear() }
                    .background(Color.Red, CircleShape)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.remove),
                    contentDescription = "Remove",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )

                Text(text = " Remove All", style = Typography.titleSmall.copy(color = Color.White))
            }
        }
    }
}

@Composable
private fun KeywordChips(keywords: SnapshotStateList<String>) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.animateContentSize()
    ) {
        keywords.forEachIndexed { index, keyword ->
            AssistChip(
                onClick = { keywords.remove(keyword) },
                shape = CircleShape,
                colors = AssistChipDefaults.assistChipColors(
                    labelColor = Color.White,
                    containerColor = MentionTextColor,
                    leadingIconContentColor = Color.White,
                    trailingIconContentColor = Color.White
                ),
                border = null,
                elevation = AssistChipDefaults.assistChipElevation(elevation = 24.dp),
                label = { Text(text = keyword) },
                leadingIcon = { Text(text = "${index + 1}.") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        modifier = Modifier.size(20.dp)
                    )
                }
            )
        }
    }
}