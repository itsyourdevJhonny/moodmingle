package com.emc.moodmingle.ui.post.audio

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient

@Composable
fun FilePicker() {
    var selectedTye by remember { mutableStateOf("Image") }
    val types = listOf("Image", "Video", "Audio")
    var expanded by remember { mutableStateOf(false) }
    var fileUri by remember { mutableStateOf<Uri?>(null) }
    var fileSize by remember { mutableLongStateOf(0L) }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            fileUri = it
            val size = getFileSize(context, it)
            fileSize = size
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Select Type", color = Color.White)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Type",
                        modifier = Modifier
                            .graphicsLayer(alpha = 0.99f)
                            .drawWithCache {
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(
                                        brush = BrushPrimaryGradient,
                                        blendMode = BlendMode.SrcAtop
                                    )
                                }
                            }
                    )

                    Text(text = selectedTye, fontStyle = FontStyle.Italic)
                }
            }

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                types.forEach { type ->
                    DropdownMenuItem(
                        onClick = {
                            selectedTye = type
                            expanded = false
                        },
                        text = { Text(type, textAlign = TextAlign.Center) }
                    )
                }
            }
        }

        Button(
            onClick = {
                val mimeType = when (selectedTye) {
                    "Image" -> "image/*"
                    "Video" -> "video/*"
                    "Audio" -> "audio/*"
                    else -> ""
                }
                launcher.launch(arrayOf(mimeType))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(brush = BrushPrimaryGradient, shape = RoundedCornerShape(8.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            content = { Text(text = "Select File", color = Color.White) }
        )

        fileUri?.let {
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                content = {
                    Text(text = "File Size:", color = Color.White, fontWeight = FontWeight.Bold)
                    Text(formatFileSize(fileSize))
                }
            )
            FilePreview(uri = it, mimeType = getMimeType(context, it))
        }
    }
}

@Composable
fun FilePreview(uri: Uri, mimeType: String) {
    when {
        mimeType.startsWith("image/") -> {
            AsyncImage(
                model = uri,
                contentDescription = "Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        mimeType.startsWith("video/") -> {
            VideoPlayer(uri = uri)
        }

        mimeType.startsWith("audio/") -> {
            AudioMediaPlayer(uri = uri)
        }

        else -> {
            Text("Unsupported file type")
        }
    }
}
