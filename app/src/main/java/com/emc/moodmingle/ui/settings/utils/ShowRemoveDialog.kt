package com.emc.moodmingle.ui.settings.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.PrimaryDark

@Composable
fun ShowRemoveDialog(onShowDialog: (Boolean) -> Unit, onLoading: (Boolean) -> Unit) {

    AlertDialog(
        containerColor = PrimaryDark,
        modifier = Modifier
            .border(
                width = 0.5.dp,
                brush = BrushPrimaryGradient,
                shape = RectangleShape
            ),
        shape = RectangleShape,
        properties = DialogProperties(),
        onDismissRequest = {},
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.remove_all_saved),
                    contentDescription = "Remove All",
                    modifier = Modifier.size(28.dp),
                    tint = Color.Red
                )

                Text("Delete all?")
            }
        },
        text = {
            Text(
                text = "Are you sure you want to delete all these items?",
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        onShowDialog(false)
                        onLoading(true)
                    },
                    modifier = Modifier.background(Color.Red, CircleShape),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text(text = "Delete All", color = Color.White)
                }

                Button(
                    onClick = { onShowDialog(false) },
                    modifier = Modifier.background(BrushPrimaryGradient, CircleShape),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text(text = "Cancel", color = Color.White)
                }
            }
        }
    )
}