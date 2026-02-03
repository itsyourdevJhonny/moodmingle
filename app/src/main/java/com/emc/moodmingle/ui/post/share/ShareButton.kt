package com.emc.moodmingle.ui.post.share

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient

@Composable
fun ShareButton(
    isShared: Boolean,
    isLoading: Boolean,
    onLoading: (Boolean) -> Unit
) {
    Button(
        onClick = { onLoading(true) },
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = if (!isLoading) BrushPrimaryGradient else SolidColor(Color.Transparent),
                shape = CircleShape
            ),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
    ) {
        if (isLoading) LoadingIndicator() else Icon(isShared)
        Label(isLoading, isShared)
    }
}

@Composable
private fun LoadingIndicator() {
    CircularProgressIndicator(
        modifier = Modifier.size(28.dp),
        color = Color.White,
        strokeWidth = 2.dp
    )
}

@Composable
private fun Icon(isShared: Boolean) {
    Icon(
        modifier = Modifier.size(20.dp),
        painter = painterResource(if (isShared) R.drawable.remove else R.drawable.share),
        contentDescription = if (isShared) "Unshare" else "Share",
        tint = Color.White
    )
}

@Composable
private fun Label(isLoading: Boolean, isShared: Boolean) {
    Text(
        modifier = Modifier.padding(start = 8.dp),
        text = if (isLoading) {
            if (isShared) "Unsharing..." else "Sharing..."
        } else {
            if (isShared) "Unshare" else "Share"
        },
        color = Color.White,
        fontWeight = FontWeight.Bold
    )
}