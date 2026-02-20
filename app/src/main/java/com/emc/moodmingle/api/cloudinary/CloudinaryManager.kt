package com.emc.moodmingle.api.cloudinary

import android.content.Context
import com.cloudinary.android.MediaManager

object CloudinaryManager {

    fun init(context: Context) {
        try {
            MediaManager.get()
        } catch (_: IllegalStateException) {
            val config = hashMapOf(
                "cloud_name" to "dswkerplv",
                "api_key" to "385216546412969",
                "api_secret" to "ZX7QyTQLZuOVk7kGt4S8bZUGeJ0",
                "secure" to true
            )

            try {
                MediaManager.init(context, config)
                println("✅ CLOUDINARY INITIALIZED WITH CONFIG")
            } catch (initError: Exception) {
                initError.printStackTrace()
                println("❌ CLOUDINARY INITIALIZATION FAILED: ${initError.message}")
            }
        }
    }

    fun getManager(): MediaManager {
        return MediaManager.get()
    }
}