package com.emc.moodmingle.utils.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.ContentPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ScaffoldHeader(
    title: String = "",
    doneLabel: String = "Done",
    enabled: Boolean = false,
    onDone: (() -> Unit)? = null,
    onBack: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            containerColor = PrimaryDark
        ),
        title = { Text(text = title, fontSize = 20.sp) },
        navigationIcon = {
            IconButton(onClick = { onBack() }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            if (onDone != null) {
                TextButton(
                    onClick = { onDone.invoke() },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = SecondaryDark,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        top = ContentPadding.calculateTopPadding(),
                        end = 16.dp,
                        bottom = ContentPadding.calculateBottomPadding()
                    ),
                    enabled = enabled
                ) {
                    Text(text = doneLabel, fontWeight = FontWeight.Bold)
                }
            }
        }
    )
}