package com.emc.moodmingle.cloudinary

import android.content.Context
import android.net.Uri
import com.cloudinary.Cloudinary
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.utils.ObjectUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

object CloudinaryService {

    private const val UPLOAD_PRESET = "moodmingle_unsigned"
    private const val CLOUD_NAME = "dswkerplv"
    private const val API_KEY = "385216546412969"
    private const val API_SECRET = "ZX7QyTQLZuOVk7kGt4S8bZUGeJ0"

    private val cloudinary = Cloudinary(
        ObjectUtils.asMap(
            "cloud_name", CLOUD_NAME,
            "api_key", API_KEY,
            "api_secret", API_SECRET
        )
    )

    fun getPublicIdFromUrl(url: String): String {
        val afterUpload = url.substringAfter("upload/")
        val withoutVersion = afterUpload.replace(Regex("^v\\d+/"), "")
        return withoutVersion.substringBeforeLast(".")
    }

    suspend fun uploadFile(
        context: Context,
        fileUri: Uri?,
        onProgress: ((uploadedBytes: Long, totalBytes: Long) -> Unit)? = null
    ): String? = suspendCancellableCoroutine { cont ->
        if (fileUri == null) {
            println("NO FILE URI PROVIDED")
            cont.resume(null)
            return@suspendCancellableCoroutine
        }

        val mimeType = context.contentResolver.getType(fileUri)
        if (mimeType == null) {
            println("CANNOT DETECT MIME TYPE")
            cont.resume(null)
            return@suspendCancellableCoroutine
        }

        val isImage = mimeType.startsWith("image/")
        val isVideo = mimeType.startsWith("video/")
        val isAudio = mimeType.startsWith("audio/")

        if (!isImage && !isVideo && !isAudio) {
            println("INVALID FILE TYPE: $mimeType")
            cont.resume(null)
            return@suspendCancellableCoroutine
        }

        val resourceType = when {
            isVideo -> "video"
            isAudio -> "auto"
            else -> "image"
        }

        val format = when {
            isVideo -> "mp4"
            isAudio -> "mp3"
            else -> "jpg"
        }

        println("DETECTED TYPE: $mimeType (RESOURCE: $resourceType, FORMAT: $format)")

        MediaManager.get()
            .upload(fileUri)
            .option("resource_type", "auto")
            .option("quality", "auto:best")
            .unsigned(UPLOAD_PRESET)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {
                    println("UPLOAD STARTED: $requestId")
                }

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    val progressPercent = (bytes.toDouble() / totalBytes * 100).toInt()
                    println("UPLOAD PROGRESS: $progressPercent%")
                    onProgress?.invoke(bytes, totalBytes) // <-- CALL PROGRESS CALLBACK
                }

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val uploadedUrl = resultData["secure_url"].toString()
                    println("UPLOAD SUCCESS: $uploadedUrl")
                    cont.resume(uploadedUrl)
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    println("UPLOAD ERROR: ${error.description}")
                    cont.resume(null)
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {
                    println("UPLOAD RESCHEDULED: ${error.description}")
                }
            })
            .dispatch()
    }

    suspend fun uploadFile(context: Context, fileUri: Uri?): String? =
        suspendCancellableCoroutine { cont ->
            if (fileUri == null) {
                println("NO FILE URI PROVIDED")
                cont.resume(null)
                return@suspendCancellableCoroutine
            }

            val mimeType = context.contentResolver.getType(fileUri)
            if (mimeType == null) {
                println("CANNOT DETECT MIME TYPE")
                cont.resume(null)
                return@suspendCancellableCoroutine
            }

            val isImage = mimeType.startsWith("image/")
            val isVideo = mimeType.startsWith("video/")
            val isAudio = mimeType.startsWith("audio/")

            if (!isImage && !isVideo && !isAudio) {
                println("INVALID FILE TYPE: $mimeType")
                cont.resume(null)
                return@suspendCancellableCoroutine
            }

            val resourceType = when {
                isVideo -> "video"
                isAudio -> "auto"
                else -> "image"
            }

            val format = when {
                isVideo -> "mp4"
                isAudio -> "mp3"
                else -> "jpg"
            }

            println("DETECTED TYPE: $mimeType (RESOURCE: $resourceType, FORMAT: $format)")

            MediaManager.get()
                .upload(fileUri)
                .option("resource_type", "auto")
                .option("quality", "auto:best")
                .unsigned(UPLOAD_PRESET)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {
                        println("UPLOAD STARTED: $requestId")
                    }

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                        val progress = (bytes.toDouble() / totalBytes * 100).toInt()
                        println("UPLOAD PROGRESS: $progress%")
                    }

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val uploadedUrl = resultData["secure_url"].toString()
                        println("UPLOAD SUCCESS: $uploadedUrl")
                        cont.resume(uploadedUrl)
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        println("UPLOAD ERROR: ${error.description}")
                        cont.resume(null)
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        println("UPLOAD RESCHEDULED: ${error.description}")
                    }
                })
                .dispatch()
        }

    suspend fun updateFile(context: Context, oldPublicId: String?, newUri: Uri): String? {
        if (oldPublicId != null) {
            deleteFile(oldPublicId)
        }
        return uploadFile(context, newUri)
    }

    suspend fun deleteFile(publicId: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap())
            println("DELETE RESULT: $result")
            result["result"] == "ok"
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
