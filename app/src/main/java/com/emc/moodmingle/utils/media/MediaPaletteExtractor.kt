package com.emc.moodmingle.utils.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object MediaPaletteExtractor {

    suspend fun extractTopBottomColors(
        context: Context,
        uri: Uri,
        isVideo: Boolean
    ): Pair<Color, Color> = withContext(Dispatchers.Default) {

        val bitmap = loadBitmap(context, uri, isVideo)
        val (topStrip, bottomStrip) = extractStrips(bitmap)

        val topColor = extractPaletteColor(topStrip)
        val bottomColor = extractPaletteColor(bottomStrip)

        topColor to bottomColor
    }

    private fun loadBitmap(
        context: Context,
        uri: Uri,
        isVideo: Boolean
    ): Bitmap {
        return if (isVideo) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                loadVideoApi28(context, uri)
            } else {
                loadVideoLegacy(context, uri)
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                loadImageApi28(context, uri)
            } else {
                loadImageLegacy(context, uri)
            }
        }
    }

    // ---------------- IMAGE ----------------
    @RequiresApi(Build.VERSION_CODES.P)
    private fun loadImageApi28(
        context: Context,
        uri: Uri,
        size: Int = 64
    ): Bitmap {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        return ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
            decoder.setTargetSize(size, size)
            decoder.isMutableRequired = false
        }
    }

    @Suppress("DEPRECATION")
    private fun loadImageLegacy(
        context: Context,
        uri: Uri
    ): Bitmap {
        val options = BitmapFactory.Options().apply { inSampleSize = 4 }

        context.contentResolver.openInputStream(uri).use {
            return BitmapFactory.decodeStream(it, null, options) ?: error("failed to decode image")
        }
    }

    // ---------------- VIDEO ----------------
    @RequiresApi(Build.VERSION_CODES.P)
    private fun loadVideoApi28(
        context: Context,
        uri: Uri,
        width: Int = 64,
        height: Int = 64
    ): Bitmap {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)

        val bitmap = retriever.getScaledFrameAtTime(
            0,
            MediaMetadataRetriever.OPTION_CLOSEST_SYNC,
            width,
            height
        ) ?: error("failed to retrieve video frame")

        retriever.release()
        return bitmap
    }

    @Suppress("DEPRECATION")
    private fun loadVideoLegacy(
        context: Context,
        uri: Uri
    ): Bitmap {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)

        val bitmap = retriever.getFrameAtTime(0) ?: error("failed to retrieve video frame")

        retriever.release()
        return bitmap
    }

    // ---------------- PALETTE ----------------
    private fun extractStrips(bitmap: Bitmap): Pair<Bitmap, Bitmap> {
        val stripHeight = (bitmap.height * 0.2f).toInt().coerceAtLeast(1)

        val top = Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            stripHeight
        )

        val bottom = Bitmap.createBitmap(
            bitmap,
            0,
            bitmap.height - stripHeight,
            bitmap.width,
            stripHeight
        )

        return top to bottom
    }

    private fun extractPaletteColor(bitmap: Bitmap): Color {
        val safeBitmap = bitmap.toSoftwareBitmap()

        val palette = Palette.from(safeBitmap)
            .maximumColorCount(8)
            .clearFilters()
            .generate()

        return when {
            palette.vibrantSwatch != null -> Color(palette.vibrantSwatch!!.rgb)
            palette.dominantSwatch != null -> Color(palette.dominantSwatch!!.rgb)
            else -> Color.Black
        }
    }

}

private fun Bitmap.toSoftwareBitmap(): Bitmap {
    return if (this.config == Bitmap.Config.HARDWARE) {
        this.copy(Bitmap.Config.ARGB_8888, false)
    } else {
        this
    }
}
