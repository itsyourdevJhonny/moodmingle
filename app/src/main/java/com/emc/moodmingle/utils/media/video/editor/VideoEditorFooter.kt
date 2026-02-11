package com.emc.moodmingle.utils.media.video.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.Typography

@Composable
fun VideoEditorFooter(selectedAction: String, onActionSelected: (String) -> Unit) {
    BottomAppBar(
        containerColor = PrimaryDark,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf(
                "Speed" to R.drawable.video_playback,
                "Trim" to R.drawable.video_trim,
                "Volume" to R.drawable.video_volume,
            ).forEach { (title, icon) ->
                val isSelected = selectedAction == title

                FooterItem(title, icon, isSelected, onActionSelected)
            }
        }
    }
}

@Composable
private fun FooterItem(
    title: String,
    icon: Int,
    isSelected: Boolean,
    onActionSelected: (String) -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ActionIconButton(onActionSelected, isSelected, title, icon)
        ActionTitle(title, isSelected)
    }
}

@Composable
private fun ActionTitle(title: String, isSelected: Boolean) {
    Text(
        text = title,
        style = Typography.bodyMedium,
        color = if (isSelected) Color.White else GrayTextColor
    )
}

@Composable
private fun ActionIconButton(
    onActionSelected: (String) -> Unit,
    isSelected: Boolean,
    title: String,
    icon: Int,
) {
    IconButton(onClick = { onActionSelected(if (isSelected) "" else title) }) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = if (isSelected) Color.White else GrayTextColor,
            modifier = Modifier
                .size(if (title == "Speed") 32.dp else 42.dp)
        )
    }
}