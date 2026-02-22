package com.emc.moodmingle.ui.create.dailymood.settings.quality

import android.graphics.Bitmap
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.domain.remote.model.post.dailymood.settings.DailyMoodSettings
import com.emc.moodmingle.domain.remote.model.post.dailymood.settings.UploadQuality
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import java.io.ByteArrayOutputStream

@Composable
fun UploadQualityScreen(settings: DailyMoodSettings, onEdit: (DailyMoodSettings) -> Unit) {
    UploadQuality.entries.forEach { quality ->
        UploadQualityItem(
            quality = quality,
            selected = settings.uploadQuality == quality,
            onSelected = { onEdit(settings.copy(uploadQuality = it)) }
        )

        Spacer(modifier = Modifier.height(12.dp))
    }

    Spacer(modifier = Modifier.height(24.dp))

    UploadQualityExplanation(settings.uploadQuality)
}

@Composable
private fun UploadQualityItem(
    quality: UploadQuality,
    selected: Boolean,
    onSelected: (UploadQuality) -> Unit,
) {
    val background = if (selected) SecondaryDark else PrimaryDark

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected(quality) },
        colors = CardDefaults.cardColors(containerColor = background)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ItemNameAndDescription(quality)
                ItemRadioButton(selected, onSelected, quality)
            }
        }
    }
}

@Composable
private fun RowScope.ItemNameAndDescription(quality: UploadQuality) {
    Column(modifier = Modifier.weight(1f)) {
        Text(
            text = quality.displayName(),
            color = Color.White,
            style = Typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = quality.description(),
            color = GrayTextColor,
            style = Typography.bodySmall
        )
    }
}

@Composable
private fun ItemRadioButton(
    selected: Boolean,
    onSelected: (UploadQuality) -> Unit,
    quality: UploadQuality,
) {
    RadioButton(
        selected = selected,
        onClick = { onSelected(quality) },
        colors = RadioButtonDefaults.colors(
            selectedColor = Color.White,
            unselectedColor = Color.White
        )
    )
}

@Composable
private fun UploadQualityExplanation(quality: UploadQuality) {
    val text = when (quality) {
        UploadQuality.HIGH -> "Your moods will be uploaded with minimal compression for the best visual clarity. This may use more data and take longer to upload."
        UploadQuality.DATA_SAVER -> "Your moods will be compressed before uploading to reduce file size. This saves data and uploads faster but may slightly reduce visual quality."
    }

    Text(
        text = text,
        color = GrayTextColor,
        style = Typography.bodySmall,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    )
}

private fun UploadQuality.displayName(): String {
    return when (this) {
        UploadQuality.HIGH -> "High Quality"
        UploadQuality.DATA_SAVER -> "Data Saver"
    }
}

private fun UploadQuality.description(): String {
    return when (this) {
        UploadQuality.HIGH -> "Better visual quality. Uses more data."
        UploadQuality.DATA_SAVER -> "Smaller file size. Faster uploads. Saves data."
    }
}

fun compressImage(bitmap: Bitmap, qualityMode: UploadQuality): ByteArray {
    val stream = ByteArrayOutputStream()

    val compressionQuality = when (qualityMode) {
        UploadQuality.HIGH -> 95
        UploadQuality.DATA_SAVER -> 70
    }

    bitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality, stream)

    return stream.toByteArray()
}
