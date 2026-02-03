package com.emc.moodmingle.ui.profile.storymood.add

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.TertiaryDark

@Composable
fun AddStoryMood(modifier: Modifier = Modifier) {
    var showAddDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .background(SecondaryDark, CircleShape)
            .border(
                width = 0.5.dp,
                color = TertiaryDark,
                shape = CircleShape
            )
            .clickable { showAddDialog = true },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(start = 4.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier.size(20.dp),
                tint = Color.White
            )

            Image(
                painter = painterResource(R.drawable.feelings_colored),
                contentDescription = "Feelings",
                modifier = Modifier.size(20.dp),
                contentScale = ContentScale.Crop
            )
        }
    }

    if (showAddDialog) {
        AddStoryMoodDialog(onDismiss = { showAddDialog = false })
    }
}