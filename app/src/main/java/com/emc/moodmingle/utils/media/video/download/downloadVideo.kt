package com.emc.moodmingle.utils.media.video.download

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

suspend fun downloadVideo(context: Context, url: String, fileName: String): File? {
    return withContext(Dispatchers.IO) {
        try {
            val file = File(context.cacheDir, fileName)
            if (file.exists()) return@withContext file

            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connect()
            val input = connection.inputStream
            val output = FileOutputStream(file)
            input.copyTo(output)
            output.flush()
            output.close()
            input.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
