package com.emc.moodmingle.utils.media.image

import androidx.compose.ui.graphics.ColorMatrix

object ImageFilters {

    fun matrix(type: ImageFilterType): ColorMatrix {
        return when (type) {
            ImageFilterType.NORMAL -> ColorMatrix()

            ImageFilterType.GRAYSCALE -> ColorMatrix().apply { setToSaturation(0f) }

            ImageFilterType.DESATURATED -> ColorMatrix().apply { setToSaturation(0.5f) }

            ImageFilterType.SEPIA ->
                ColorMatrix(
                    floatArrayOf(
                        0.393f, 0.769f, 0.189f, 0f, 0f,
                        0.349f, 0.686f, 0.168f, 0f, 0f,
                        0.272f, 0.534f, 0.131f, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )

            ImageFilterType.VINTAGE ->
                ColorMatrix(
                    floatArrayOf(
                        0.9f, 0.5f, 0.1f, 0f, 0f,
                        0.3f, 0.8f, 0.1f, 0f, 0f,
                        0.2f, 0.3f, 0.5f, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )

            ImageFilterType.COOL ->
                ColorMatrix(
                    floatArrayOf(
                        0.9f, 0f, 0.1f, 0f, 0f,
                        0f, 1f, 0.1f, 0f, 0f,
                        0f, 0f, 1.2f, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )

            ImageFilterType.WARM ->
                ColorMatrix(
                    floatArrayOf(
                        1.2f, 0f, 0f, 0f, 0f,
                        0f, 1.1f, 0f, 0f, 0f,
                        0f, 0f, 0.9f, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )

            ImageFilterType.FADE ->
                ColorMatrix(
                    floatArrayOf(
                        1f, 0f, 0f, 0f, 30f,
                        0f, 1f, 0f, 0f, 30f,
                        0f, 0f, 1f, 0f, 30f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )

            ImageFilterType.DRAMATIC -> {

                val saturation = ColorMatrix().apply {
                    setToSaturation(1.6f)
                }

                val contrast = ColorMatrix(
                    floatArrayOf(
                        1.4f, 0f, 0f, 0f, -40f,
                        0f, 1.4f, 0f, 0f, -40f,
                        0f, 0f, 1.4f, 0f, -40f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )

                multiplyColorMatrices(saturation, contrast)
            }

            ImageFilterType.BRIGHT ->
                ColorMatrix(
                    floatArrayOf(
                        1.2f, 0f, 0f, 0f, 20f,
                        0f, 1.2f, 0f, 0f, 20f,
                        0f, 0f, 1.2f, 0f, 20f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )

            ImageFilterType.CONTRAST ->
                ColorMatrix(
                    floatArrayOf(
                        1.4f, 0f, 0f, 0f, -40f,
                        0f, 1.4f, 0f, 0f, -40f,
                        0f, 0f, 1.4f, 0f, -40f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )

            ImageFilterType.SOFT -> ColorMatrix().apply { setToSaturation(0.8f) }

            ImageFilterType.HARD -> ColorMatrix().apply { setToSaturation(1.5f) }

            ImageFilterType.MOODY -> ColorMatrix(
                floatArrayOf(
                    1.1f, 0f, 0f, 0f, -20f,
                    0f, 1f, 0f, 0f, -20f,
                    0f, 0f, 0.9f, 0f, -20f,
                    0f, 0f, 0f, 1f, 0f
                )
            )

            ImageFilterType.SUNNY -> ColorMatrix(
                floatArrayOf(
                    1.3f, 0f, 0f, 0f, 10f,
                    0f, 1.2f, 0f, 0f, 10f,
                    0f, 0f, 1f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            )

            ImageFilterType.NIGHT -> ColorMatrix(
                floatArrayOf(
                    0.8f, 0f, 0f, 0f, -30f,
                    0f, 0.8f, 0f, 0f, -30f,
                    0f, 0f, 1.1f, 0f, -30f,
                    0f, 0f, 0f, 1f, 0f
                )
            )

            ImageFilterType.FILM -> {
                val saturationMatrix = ColorMatrix().apply {
                    setToSaturation(0.9f)
                }

                val brightnessMatrix = ColorMatrix(
                    floatArrayOf(
                        1.1f, 0f, 0f, 0f, 5f,
                        0f, 1.1f, 0f, 0f, 5f,
                        0f, 0f, 1.1f, 0f, 5f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )

                multiplyColorMatrices(saturationMatrix, brightnessMatrix)
            }

            ImageFilterType.LOFI -> ColorMatrix(
                floatArrayOf(
                    1.5f, 0f, 0f, 0f, -50f,
                    0f, 1.5f, 0f, 0f, -50f,
                    0f, 0f, 1.5f, 0f, -50f,
                    0f, 0f, 0f, 1f, 0f
                )
            )

            ImageFilterType.BLUSH -> ColorMatrix(
                floatArrayOf(
                    1.2f, 0f, 0f, 0f, 10f,
                    0f, 1f, 0f, 0f, 0f,
                    0f, 0f, 1f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            )

            ImageFilterType.CYAN -> ColorMatrix(
                floatArrayOf(
                    0.9f, 0f, 0.2f, 0f, 0f,
                    0f, 1f, 0.2f, 0f, 0f,
                    0f, 0f, 1.2f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            )

            ImageFilterType.AMBER -> ColorMatrix(
                floatArrayOf(
                    1.3f, 0f, 0f, 0f, 15f,
                    0f, 1.1f, 0f, 0f, 5f,
                    0f, 0f, 0.8f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        }
    }
}

fun multiplyColorMatrices(
    first: ColorMatrix,
    second: ColorMatrix
): ColorMatrix {

    val result = FloatArray(20)
    val a = first.values
    val b = second.values

    for (row in 0..3) {
        for (col in 0..4) {
            result[row * 5 + col] =
                a[row * 5 + 0] * b[0 * 5 + col] +
                        a[row * 5 + 1] * b[1 * 5 + col] +
                        a[row * 5 + 2] * b[2 * 5 + col] +
                        a[row * 5 + 3] * b[3 * 5 + col] +
                        if (col == 4) a[row * 5 + 4] else 0f
        }
    }

    return ColorMatrix(result)
}

enum class ImageFilterType {
    NORMAL,
    GRAYSCALE,
    SEPIA,
    VINTAGE,
    COOL,
    WARM,
    FADE,
    DRAMATIC,
    BRIGHT,
    CONTRAST,
    SOFT,
    HARD,
    MOODY,
    SUNNY,
    NIGHT,
    FILM,
    LOFI,
    BLUSH,
    CYAN,
    AMBER,
    DESATURATED
}