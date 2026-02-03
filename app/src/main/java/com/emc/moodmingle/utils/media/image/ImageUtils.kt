package com.emc.moodmingle.utils.media.image

import android.content.Context
import coil.ImageLoader
import coil.decode.VideoFrameDecoder

object ImageUtils {
    fun provideVideoImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .components { add(VideoFrameDecoder.Factory()) }
            .build()
    }
}