package com.emc.moodmingle.ui.dailymood.settings.shareplatform

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.dailymood.settings.DailyMoodSettings
import com.emc.moodmingle.data.firebase.model.post.dailymood.settings.SharePlatformType
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.ScaffoldHeader

@Composable
fun SharePlatformScreen(
    settings: DailyMoodSettings,
    onSettingsEdited: (DailyMoodSettings) -> Unit,
    onDismiss: () -> Unit,
) {

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            ScaffoldHeader(
                title = "Auto Share"
            ) { onDismiss() }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Automatically share your daily mood to another platform after posting.",
                color = Color.White,
                style = Typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            SharePlatformGrid(
                selected = settings.sharePlatformType,
                onSelected = { selectedPlatform ->
                    onSettingsEdited(
                        settings.copy(
                            sharePlatformType = selectedPlatform
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = when (settings.sharePlatformType) {
                    SharePlatformType.NONE -> "Your mood will only be posted inside this app."
                    SharePlatformType.FACEBOOK -> "Your mood will also be shared to Facebook."
                    SharePlatformType.INSTAGRAM -> "Your mood will also be shared to Instagram."
                    SharePlatformType.X -> "Your mood will also be shared to X."
                    SharePlatformType.THREADS -> "Your mood will also be shared to Threads."
                },
                color = GrayTextColor,
                style = Typography.bodySmall,
                modifier = Modifier.animateContentSize()
            )
        }
    }
}

@Composable
private fun SharePlatformGrid(
    selected: SharePlatformType,
    onSelected: (SharePlatformType) -> Unit,
) {
    val platforms = listOf(
        SharePlatformType.FACEBOOK,
        SharePlatformType.INSTAGRAM,
        SharePlatformType.X,
        SharePlatformType.THREADS
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.heightIn(max = 400.dp)
    ) {
        items(platforms) { platform ->
            SharePlatformCard(
                platform = platform,
                selected = selected == platform,
                onClick = { onSelected(platform) }
            )
        }
    }
}

@Composable
private fun SharePlatformCard(
    platform: SharePlatformType,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (selected) Color.White else Color.Gray
    val backgroundColor = if (selected) SecondaryDark else PrimaryDark

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(platform.toIcon()),
                contentDescription = null,
                modifier = Modifier.size(42.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = platform.displayName(), color = Color.White, style = Typography.bodyMedium)
        }
    }
}

private fun SharePlatformType.toIcon(): Int {
    return when (this) {
        SharePlatformType.NONE -> R.drawable.none_image
        SharePlatformType.FACEBOOK -> R.drawable.facebook
        SharePlatformType.INSTAGRAM -> R.drawable.instragram
        SharePlatformType.X -> R.drawable.x_black
        SharePlatformType.THREADS -> R.drawable.threads_black
    }
}

private fun SharePlatformType.displayName(): String {
    return when (this) {
        SharePlatformType.NONE -> "None"
        SharePlatformType.FACEBOOK -> "Facebook"
        SharePlatformType.INSTAGRAM -> "Instagram"
        SharePlatformType.X -> "X"
        SharePlatformType.THREADS -> "Threads"
    }
}