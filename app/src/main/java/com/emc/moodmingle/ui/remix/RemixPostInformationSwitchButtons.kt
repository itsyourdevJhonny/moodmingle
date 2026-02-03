package com.emc.moodmingle.ui.remix

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.remix.RemixEntity
import com.emc.moodmingle.utils.components.SwitchButton
import com.emc.moodmingle.utils.modifier.drawGradient

@Composable
fun BoxScope.RemixPostInformationSwitchButtons(
    entity: Any?,
    useHashtag: Boolean,
    useCaption: Boolean,
    useDescription: Boolean,
    onUseHashtag: (Boolean) -> Unit,
    onUseCaption: (Boolean) -> Unit,
    onUseDescription: (Boolean) -> Unit,
    onHashtagChanged: (String) -> Unit,
    onCaptionChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit
) {
    val remix = entity as RemixEntity?

    Column(
        modifier = Modifier
            .padding(vertical = 16.dp, horizontal = 8.dp)
            .align(Alignment.TopCenter)
    ) {
        listOf(
            Triple(R.drawable.hashtag, "hashtag", useHashtag),
            Triple(R.drawable.caption, "caption", useCaption),
            Triple(R.drawable.description, "description", useDescription)
        ).forEach { (icon, label, isUsed) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                InformationIcon(icon)

                InformationSwitchButton(
                    remix,
                    label,
                    isUsed,
                    onUseHashtag,
                    onUseCaption,
                    onUseDescription,
                    onHashtagChanged,
                    onCaptionChanged,
                    onDescriptionChanged
                )
            }
        }
    }
}

@Composable
private fun InformationSwitchButton(
    remix: RemixEntity?,
    label: String,
    isUsed: Boolean,
    onUseHashtag: (Boolean) -> Unit,
    onUseCaption: (Boolean) -> Unit,
    onUseDescription: (Boolean) -> Unit,
    onHashtagChanged: (String) -> Unit,
    onCaptionChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
) {
    SwitchButton(
        padding = 4.dp,
        label = "Use post $label",
        isChecked = isUsed,
        onCheckedChange = { isChecked ->
            when (label) {
                "hashtag" -> {
                    onUseHashtag(isChecked)
                    onHashtagChanged(if (isChecked) remix?.hashtag.orEmpty() else "")
                }

                "caption" -> {
                    onUseCaption(isChecked)
                    onCaptionChanged(if (isChecked) remix?.caption.orEmpty() else "")
                }

                "description" -> {
                    onUseDescription(isChecked)
                    onDescriptionChanged(if (isChecked) remix?.description.orEmpty() else "")
                }
            }
        }
    )
}

@Composable
private fun InformationIcon(icon: Int) {
    Icon(
        painter = painterResource(icon),
        contentDescription = "Hashtag",
        modifier = Modifier
            .size(20.dp)
            .drawGradient()
    )
}