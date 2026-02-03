package com.emc.moodmingle.ui.video.comment.more.secondary.trigger

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography

@Composable
fun TriggerThirdPage(
    selectedTriggerOption: String,
    onSelectedTriggerOption: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryDark),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SuccessIcon()
            Title()
            Subtitle()
            ReportContent(selectedTriggerOption)
            BackButton(onSelectedTriggerOption, onDismiss)
        }
    }
}

@Composable
private fun ReportContent(selectedTriggerOption: String) {
    Box(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .background(SecondaryDark, RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.triggering),
                contentDescription = "Triggering",
                tint = Color.Red,
                modifier = Modifier.size(32.dp)
            )

            Text(text = selectedTriggerOption, color = Color.White)
        }
    }
}

@Composable
private fun SuccessIcon() {
    Box(
        modifier = Modifier
            .size(68.dp)
            .background(Color.Green.copy(alpha = 0.4f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .background(Color.Green.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Check",
                tint = Color.White,
                modifier = Modifier.size(38.dp)
            )
        }
    }
}

@Composable
private fun Title() {
    Text(
        text = "Trigger report submitted successfully.",
        style = Typography.bodyLarge.copy(
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        ),
        modifier = Modifier.padding(top = 32.dp)
    )
}

@Composable
private fun Subtitle() {
    Text(
        text = "Our moderation system will review it and will take an action as soon as possible.",
        style = Typography.bodyMedium.copy(color = GrayTextColor, textAlign = TextAlign.Center)
    )
}

@Composable
private fun BackButton(onSelectedTriggerOption: (String) -> Unit, onDismiss: () -> Unit) {
    Button(
        onClick = {
            onSelectedTriggerOption("")
            onDismiss()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        modifier = Modifier.background(BrushPrimaryGradient, CircleShape)
    ) {
        Text(text = "Back to Comment Section", fontWeight = FontWeight.Bold)
    }
}