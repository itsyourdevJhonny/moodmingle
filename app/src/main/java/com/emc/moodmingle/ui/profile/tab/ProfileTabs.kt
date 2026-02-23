package com.emc.moodmingle.ui.profile.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.BrushGrayGradient
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography

@Composable
fun ProfileTabs(onSelectedTab: (String) -> Unit) {
    val tabs = listOf(
        R.drawable.media to "All",
        R.drawable.text to "Text",
        R.drawable.image_video to "Media"
    )

    var selectedIcon by rememberSaveable { mutableIntStateOf(R.drawable.media) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        tabs.forEach { tab ->
            val icon = tab.first
            val isSelected = icon == selectedIcon

            Box(
                modifier = Modifier
                    .width(80.dp)
                    .background(SecondaryDark, RoundedCornerShape(8.dp))
                    .border(
                        width = if (isSelected) 2.dp else 0.5.dp,
                        brush = if (isSelected) BrushPrimaryGradient else BrushGrayGradient,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        selectedIcon = icon
                        onSelectedTab(tab.second)
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = "Tab Icon",
                        modifier = Modifier.size(20.dp)
                    )

                    Text(text = tab.second, style = Typography.bodySmall.copy(color = Color.White))
                }
            }
        }
    }
}