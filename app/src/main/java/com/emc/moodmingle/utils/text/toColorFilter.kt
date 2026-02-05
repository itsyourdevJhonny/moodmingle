package com.emc.moodmingle.utils.text

import androidx.compose.ui.graphics.ColorFilter
import com.emc.moodmingle.utils.media.image.ImageFilterType
import com.emc.moodmingle.utils.media.image.ImageFilters

fun String.toColorFilter(): ColorFilter {
    return ColorFilter.colorMatrix(ImageFilters.matrix(ImageFilterType.valueOf(this)))
}