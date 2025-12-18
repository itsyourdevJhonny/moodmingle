package com.emc.moodmingle.ui.settings.personal

import android.content.Context
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PurpleDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.utils.modifier.drawGradient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Information(
    title: String,
    @DrawableRes iconRes: Int,
    currentValue: String,
    onSave: (String, String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isClicked by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var value by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(currentValue) {
        if (!isEditing) value = currentValue
    }

    Column {
        InformationHeader(title = title, iconRes = iconRes, isEditing = isEditing)

        EditableTextField(
            value = value,
            title = title,
            isEditing = isEditing,
            onValueChange = { value = it }
        )

        ActionButtons(
            value = value,
            isEditing = isEditing,
            isClicked = isClicked,
            onCancel = {
                value = if (value.isNotEmpty() && value == currentValue) value else currentValue
                isEditing = false
            },
            onClick = { newValue ->
                handleSaveClick(
                    scope = scope,
                    context = context,
                    title = title,
                    value = newValue,
                    currentValue = currentValue,
                    isEditing = isEditing,
                    isClicked = { isClicked = it },
                    onEditingChange = { isEditing = it },
                    onSave = onSave
                )
            }
        )
    }
}

@Composable
private fun InformationHeader(title: String, @DrawableRes iconRes: Int, isEditing: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = title,
            modifier = Modifier
                .size(if (title != "Bio") 22.dp else 28.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawGradient()
        )

        Text(
            text = if (isEditing) "Editing ${title.lowercase()}..." else title,
            style = MaterialTheme.typography.titleSmall.copy(
                color = if (isEditing) {
                    val infiniteTransition = rememberInfiniteTransition()
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.6f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(800, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        )
                    )

                    Color.White.copy(alpha = alpha)
                } else {
                    GrayTextColor
                }
            )
        )
    }
}

@Composable
private fun EditableTextField(
    value: String,
    title: String,
    isEditing: Boolean,
    onValueChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SecondaryDark, RoundedCornerShape(8.dp))
    ) {
        TextField(
            value = value.ifBlank { "No ${title.lowercase()}" },
            onValueChange = onValueChange,
            enabled = isEditing,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = SecondaryDark,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = SecondaryDark,
                focusedIndicatorColor = Color.Transparent,
                disabledContainerColor = SecondaryDark,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = PurpleDark
            ),
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
private fun ActionButtons(
    value: String,
    isEditing: Boolean,
    isClicked: Boolean,
    onCancel: () -> Unit,
    onClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        if (isEditing && !isClicked) {
            TextButton(onClick = onCancel) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel",
                    tint = Color.Red,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = "Cancel",
                    style = MaterialTheme.typography.titleSmall.copy(color = Color.White)
                )
            }
        }

        TextButton(onClick = { onClick(value) }) {
            if (isClicked) {
                UpdatingIndicator()
            } else {
                EditOrSaveButtonContent(isEditing = isEditing)
            }
        }
    }
}

@Composable
private fun UpdatingIndicator() {
    CircularProgressIndicator(
        modifier = Modifier
            .padding(end = 4.dp)
            .size(20.dp),
        color = Color.White,
        strokeWidth = 2.dp
    )
    Text(
        text = "Updating...",
        style = MaterialTheme.typography.titleSmall.copy(color = GrayTextColor)
    )
}

@Composable
private fun EditOrSaveButtonContent(isEditing: Boolean) {
    Icon(
        imageVector = if (isEditing) Icons.Default.Done else Icons.Default.Edit,
        contentDescription = "Edit",
        modifier = Modifier
            .size(18.dp)
            .graphicsLayer(alpha = 0.99f)
            .drawGradient()
    )
    Text(
        modifier = Modifier.padding(start = 4.dp),
        text = if (isEditing) "Save" else "Edit",
        style = MaterialTheme.typography.titleSmall.copy(color = Color.White)
    )
}

private fun handleSaveClick(
    scope: CoroutineScope,
    context: Context,
    title: String,
    value: String,
    currentValue: String,
    isEditing: Boolean,
    isClicked: (Boolean) -> Unit,
    onEditingChange: (Boolean) -> Unit,
    onSave: (String, String) -> Unit
) {
    scope.launch {
        if (!isEditing) {
            onEditingChange(true)
            return@launch
        }

        if (value.isBlank()) {
            Toast.makeText(context, "$title cannot be blank or empty.", Toast.LENGTH_SHORT).show()
            return@launch
        }

        if (value == currentValue) {
            Toast.makeText(context, "Cannot use your current $title as new.", Toast.LENGTH_SHORT)
                .show()
            return@launch
        }

        isClicked(true)
        delay(5000)
        isClicked(false)
        onEditingChange(false)

        val type = when (title) {
            "Username" -> "username"
            "Bio" -> "bio"
            "Email" -> "email"
            else -> ""
        }

        onSave(value, type)
    }
}
