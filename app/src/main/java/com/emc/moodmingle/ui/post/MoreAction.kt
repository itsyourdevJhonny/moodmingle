package com.emc.moodmingle.ui.post

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.SecondaryDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreAction() {
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Icon(
        painter = painterResource(id = R.drawable.more),
        contentDescription = "More",
        tint = Color.White,
        modifier = Modifier
            .clickable { showSheet = true }
    )

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = Color(0xFF121212)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .background(SecondaryDark, RoundedCornerShape(16.dp))
            ) {
                CreateMoreAction(
                    R.drawable.save_post,
                    "Save Post",
                    "Save this post for later viewing or sharing."
                ) { showSheet = false }

                CreateMoreAction(
                    R.drawable.add_to_favorite,
                    "Add to Favorites",
                    "Add this post to your favorites list."
                ) { showSheet = false }

                CreateMoreAction(
                    R.drawable.share_post,
                    "Share Post",
                    "Share this post to your profile."
                ) { showSheet = false }
            }
        }
    }
}

@Composable
fun CreateMoreAction(
    @DrawableRes iconRes: Int,
    title: String,
    description: String,
    onClick: (Boolean) -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable {
                onClick(true)

                Toast.makeText(
                    context,
                    when (title) {
                        "Save Post" -> "Post saved"
                        "Add to Favorites" -> "Post added to favorites"
                        "Share Post" -> "Post shared"
                        else -> ""
                    },
                    Toast.LENGTH_SHORT
                ).show()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = "More",
            tint = Color.White,
            modifier = Modifier
                .graphicsLayer(alpha = 0.99f)
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            brush = BrushPrimaryGradient,
                            blendMode = BlendMode.SrcAtop
                        )
                    }
                }
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White
                )
            )
            Text(
                text = description,
                fontSize = 12.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMoreAction() {
    MoreAction()
}