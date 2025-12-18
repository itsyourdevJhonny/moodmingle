package com.emc.moodmingle.ui.post.action.more

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.app.Activity
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

fun saveToGallery(context: Context, bitmap: Bitmap) {
    val filename = "moodmingle_${System.currentTimeMillis()}.png"

    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MoodMingle")
        put(MediaStore.Images.Media.IS_PENDING, 1)
    }

    val uri = context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
    ) ?: return

    context.contentResolver.openOutputStream(uri)?.use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }

    contentValues.clear()
    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
    context.contentResolver.update(uri, contentValues, null, null)
}

fun shareImage(context: Context, bitmap: Bitmap) {
    val filename = "share_${System.currentTimeMillis()}.png"
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MoodMingle")
    }

    val uri = context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
    ) ?: return

    context.contentResolver.openOutputStream(uri)?.use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(intent, "Share screenshot"))
}

// extension helper to get Activity from LocalContext
fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is android.content.ContextWrapper -> baseContext.findActivity()
        else -> null
    }


// suspend capture that uses PixelCopy when possible, otherwise falls back safely
suspend fun safeCaptureViewBitmap(view: View): Bitmap = withContext(Dispatchers.Main) {
    val width = view.width.takeIf { it > 0 } ?: throw IllegalStateException("view width is zero")
    val height = view.height.takeIf { it > 0 } ?: throw IllegalStateException("view height is zero")
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val activity = view.context.findActivity()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && activity != null && view.isAttachedToWindow) {
        suspendCancellableCoroutine<Bitmap> { continuation ->
            val location = IntArray(2)
            view.getLocationInWindow(location)
            val rect = android.graphics.Rect(location[0], location[1], location[0] + width, location[1] + height)
            try {
                PixelCopy.request(activity.window, rect, bitmap, { result ->
                    if (!continuation.isActive) return@request
                    if (result == PixelCopy.SUCCESS) {
                        continuation.resume(bitmap)
                    } else {
                        try {
                            val fallback = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                            Canvas(fallback).apply { view.draw(this) }
                            continuation.resume(fallback)
                        } catch (e: Exception) { continuation.resumeWithException(e) }
                    }
                }, Handler(Looper.getMainLooper()))
            } catch (e: Exception) {
                try {
                    val fallback = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    Canvas(fallback).apply { view.draw(this) }
                    continuation.resume(fallback)
                } catch (ex: Exception) { continuation.resumeWithException(ex) }
            }
        }
    } else {
        Canvas(bitmap).apply { view.draw(this) }
        bitmap
    }
}
/*
suspend fun safeCaptureViewBitmap(view: View): Bitmap = withContext(Dispatchers.Main) {

    val width = view.width.takeIf { it > 0 } ?: throw IllegalStateException("view width is zero")
    val height = view.height.takeIf { it > 0 } ?: throw IllegalStateException("view height is zero")

    // ALWAYS SOFTWARE — NEVER HARDWARE
    val softwareBitmap = createBitmap(width, height)

    val activity = view.context.findActivity()

    // use pixelcopy if possible
    if (activity != null && view.isAttachedToWindow) {
        return@withContext suspendCancellableCoroutine { continuation ->

            val location = IntArray(2)
            view.getLocationInWindow(location)
            val rect = Rect(location[0], location[1], location[0] + width, location[1] + height)

            try {
                PixelCopy.request(
                    activity.window,
                    rect,
                    softwareBitmap,
                    { copyResult ->

                        if (!continuation.isActive) return@request

                        if (copyResult == PixelCopy.SUCCESS) {
                            continuation.resume(softwareBitmap)
                        } else {
                            // fallback draw
                            try {
                                val fallback = createBitmap(width, height)
                                Canvas(fallback).apply { view.draw(this) }
                                continuation.resume(fallback)
                            } catch (e: Exception) {
                                continuation.resumeWithException(e)
                            }
                        }
                    },
                    Handler(Looper.getMainLooper())
                )
            } catch (e: Exception) {
                // pixelcopy crashed → fallback
                try {
                    val fallback = createBitmap(width, height)
                    Canvas(fallback).apply { view.draw(this) }
                    continuation.resume(fallback)
                } catch (e2: Exception) {
                    continuation.resumeWithException(e2)
                }
            }
        }
    }

    // fallback (no pixelcopy)
    Canvas(softwareBitmap).apply { view.draw(this) }
    return@withContext softwareBitmap
}*/
