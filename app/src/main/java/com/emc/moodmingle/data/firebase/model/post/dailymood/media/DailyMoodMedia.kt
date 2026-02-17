package com.emc.moodmingle.data.firebase.model.post.dailymood.media

import android.os.Parcelable
import com.emc.moodmingle.utils.media.image.ImageFilterType
import com.emc.moodmingle.utils.media.video.editor.VideoEditorState
import kotlinx.parcelize.Parcelize

@Parcelize
data class DailyMoodMedia(
    val type: DailyMoodMediaType = DailyMoodMediaType.SINGLE,
    val urls: List<String> = emptyList(),
    val image: DailyMoodImage = DailyMoodImage(),
    val video: VideoEditorState = VideoEditorState(),
) : Parcelable

enum class DailyMoodMediaType {
    SINGLE,
    COLLAGE,
    LAYOUT
}

@Parcelize
data class DailyMoodImage(
    val filterName: String = ImageFilterType.NORMAL.name,
    val shapeType: ShapeType = ShapeType.NORMAL,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
) : Parcelable

enum class ShapeType {
    NORMAL,
    CIRCLE,
    ROUNDED,
    CUT,
    OVAL_HORIZONTAL,
    OVAL_VERTICAL,
    TRIANGLE,
    DIAMOND,
    HEXAGON,
    PENTAGON,
    OCTAGON,
    STAR,
    HEART,
    BLOB,
    PARALLELOGRAM,
    TRAPEZOID,
    CHEVRON,
    MESSAGE_BUBBLE
}