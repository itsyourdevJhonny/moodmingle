package com.emc.moodmingle.ui.settings.saved.collection

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.saved.CollectionEntityFirebase
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.LoadingDialog
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.remote.saved.CollectionViewModelFirebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CreateCollectionDialog(onDismiss: () -> Unit, userUid: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val collectionViewModelFirebase = hiltViewModel<CollectionViewModelFirebase>()
    var name by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PrimaryDark),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 48.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .align(Alignment.Start)
                        .clickable { onDismiss() }
                )

                Icon(
                    painter = painterResource(R.drawable.collections),
                    contentDescription = "Add",
                    tint = Color.White,
                    modifier = Modifier
                        .size(48.dp)
                        .graphicsLayer(alpha = 0.99f)
                        .drawGradient()
                )

                Text(
                    text = "Create Collection",
                    style = Typography.titleLarge.copy(color = Color.White)
                )

                Text(
                    text = "The collection lets you save posts in one place so you can easily find, organize, and revisit the content that matters to you anytime.",
                    style = Typography.titleSmall.copy(
                        color = GrayTextColor,
                        textAlign = TextAlign.Center
                    )
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Collection Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedLabelColor = Color.White
                    )
                )

                Button(
                    onClick = {
                        if (name.isBlank()) {
                            Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isLoading = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .background(BrushPrimaryGradient, CircleShape)
                        .fillMaxWidth()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Create",
                            color = Color.White
                        )
                    }
                }
            }
        }

        if (isLoading) {
            LoadingDialog("Creating collection") {
                scope.launch {
                    collectionViewModelFirebase.insert(
                        CollectionEntityFirebase(
                            userUid = userUid,
                            name = name,
                            time = System.currentTimeMillis()
                        )
                    )

                    delay(2000)

                    isLoading = false
                    onDismiss()
                }
            }
        }
    }
}