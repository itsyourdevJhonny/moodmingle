package com.emc.moodmingle.ui.profile.utils

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.post.image.downloadImage
import com.emc.moodmingle.ui.post.image.isImageInGallery
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.utils.components.dialogFullSizeProperties
import com.emc.moodmingle.utils.modifier.drawGradient

@Composable
fun ShowProfilePicture(avatarUrl: String, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val fileName = avatarUrl.substringAfterLast("/")

    Dialog(
        onDismissRequest = onDismiss,
        properties = dialogFullSizeProperties()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PrimaryDark)
                .padding(bottom = 42.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = "Avatar"
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 20.dp, y = 28.dp)
                    .clickable { onDismiss() }
            )

            Box(
                modifier = Modifier
                    .padding(end = 16.dp, bottom = 20.dp)
                    .align(Alignment.BottomEnd)
                    .clickable {
                        if (isImageInGallery(fileName)) {
                            Toast.makeText(context, "Image already saved", Toast.LENGTH_SHORT)
                                .show()
                            return@clickable
                        }

                        downloadImage(context, avatarUrl, fileName)
                        Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT)
                            .show()
                    }
            ) {
                Icon(
                    painter = painterResource(R.drawable.download_outlined),
                    contentDescription = "Download",
                    modifier = Modifier
                        .size(32.dp)
                        .graphicsLayer(alpha = 0.7f)
                        .drawGradient()
                )
            }
        }
    }
}