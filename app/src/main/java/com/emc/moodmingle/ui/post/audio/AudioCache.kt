package com.emc.moodmingle.ui.post.audio

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

@UnstableApi
object AudioCache {

    private var cache: SimpleCache? = null

    @OptIn(UnstableApi::class)
    fun get(context: Context): SimpleCache {
        if (cache == null) {
            cache = SimpleCache(
                File(context.cacheDir, "audio_cache"),
                LeastRecentlyUsedCacheEvictor(100L * 1024 * 1024)
            )
        }
        return cache!!
    }
}
