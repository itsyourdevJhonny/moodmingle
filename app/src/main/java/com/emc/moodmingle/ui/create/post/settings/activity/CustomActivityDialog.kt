package com.emc.moodmingle.ui.create.post.settings.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.emc.moodmingle.utils.modifier.roundedGradientBorder

@Composable
fun CustomActivityDialog(
    customActivity: String,
    onCustomActivityChange: (String) -> Unit,
    onActivitySelected: (ActivityItem?) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                .roundedGradientBorder(12.dp)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Create Your Activity", color = Color.White, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = customActivity,
                onValueChange = {
                    onCustomActivityChange(it)
                    onActivitySelected(null)
                },
                placeholder = {
                    Text(text = "Type your own activity..", fontSize = 14.sp, color = Color.Gray)
                },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}