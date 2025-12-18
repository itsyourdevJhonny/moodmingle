package com.emc.moodmingle.ui.settings.encryption

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient

@Composable
fun DisplayEncryptedContent(
    title: String,
    originalValue: String,
    currentEncryptedValue: String,
    encryptedValue: String
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (title != "Post") {
            Text(
                text = "Original Value: $originalValue",
                style = Typography.bodySmall.copy(color = GrayTextColor),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = "Encrypted Value:",
            style = Typography.bodySmall.copy(color = GrayTextColor),
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Box(modifier = Modifier.background(SecondaryDark, RoundedCornerShape(8.dp))) {
            Text(
                text = currentEncryptedValue,
                style = Typography.bodySmall.copy(color = Color.LightGray),
                modifier = Modifier.padding(8.dp)
            )
        }

        if (title != "Post") {
            Icon(
                painter = painterResource(R.drawable.copy),
                modifier = Modifier
                    .align(Alignment.End)
                    .size(14.dp)
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient()
                    .clickable {
                        val clipboardManager =
                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clipData = ClipData.newPlainText("text", encryptedValue)
                        clipboardManager.setPrimaryClip(clipData)

                        Toast.makeText(
                            context,
                            "Encrypted value copied to clipboard",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                contentDescription = "Copy"
            )
        }
    }
}