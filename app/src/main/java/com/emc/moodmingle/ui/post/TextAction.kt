package com.emc.moodmingle.ui.post

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

@Composable
fun TextAction(@DrawableRes id: Int, description: String) {
    val context = LocalContext.current

    Icon(
        painter = painterResource(id),
        contentDescription = description,
        tint = Color.White,
        modifier = Modifier.clickable {
            Toast.makeText(
                context,
                "Sharing mood post...",
                Toast.LENGTH_LONG
            ).show()
        }
    )
}