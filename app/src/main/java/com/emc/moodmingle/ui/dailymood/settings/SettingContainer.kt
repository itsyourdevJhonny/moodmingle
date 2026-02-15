package com.emc.moodmingle.ui.dailymood.settings

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.utils.components.ScaffoldHeader

@Composable
fun SettingContainer(
    title: String,
    contents: List<Pair<Int, Any>>,
    type: Any,
    isImage: Boolean = false,
    size: Dp = 20.dp,
    onSelected: (Any) -> Unit,
    onDismiss: () -> Unit,
) {
    BackHandler { onDismiss() }

    Scaffold(
        containerColor = Color.Black,
        topBar = { ScaffoldHeader(title = title) { onDismiss() } }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            contents.forEach { (contentIcon, contentType) ->
                val isSelected = contentType == type
                SettingItem(
                    title = (contentType as Enum<*>).name,
                    contentIcon,
                    isSelected,
                    isImage,
                    size,
                    onClick = { onSelected(contentType) }
                )
            }
        }
    }
}


@Composable
private fun SettingItem(
    title: String,
    @DrawableRes icon: Int,
    isSelected: Boolean,
    isImage: Boolean,
    size: Dp,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ItemIcon(isImage, icon, size)
            ItemTitle(title)
        }

        ItemRadioButton(isSelected)
    }
}

@Composable
private fun ItemIcon(
    isImage: Boolean,
    icon: Int,
    size: Dp,
) {
    if (isImage) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(size)
        )
    } else {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(size)
        )
    }
}

@Composable
private fun ItemTitle(title: String) {
    Text(
        text = title
            .split("_")
            .joinToString(" ") { text -> text.lowercase().replaceFirstChar { it.titlecase() } },
        color = Color.White
    )
}

@Composable
private fun ItemRadioButton(isSelected: Boolean) {
    RadioButton(
        selected = isSelected,
        onClick = null,
        colors = RadioButtonDefaults.colors(
            selectedColor = Color.White,
            unselectedColor = Color.White
        )
    )
}