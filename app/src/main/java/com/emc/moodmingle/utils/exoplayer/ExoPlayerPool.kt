package com.emc.moodmingle.utils.exoplayer

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class ExoPlayerPool(
    private val context: Context,
    private val maxSize: Int = 3
) {
    private val pool = ArrayDeque<ExoPlayer>()
    private val preloadedUrls = mutableSetOf<String>()

    /**
     * Acquire a player for a specific video URL.
     * If a player is available in the pool, reuse it.
     * Otherwise, create a new ExoPlayer instance.
     */
    fun acquire(videoUrl: String): ExoPlayer {
        val player = if (pool.isNotEmpty()) {
            pool.removeFirst()
        } else {
            ExoPlayer.Builder(context).build()
        }

        player.setMediaItem(MediaItem.fromUri(videoUrl))
        player.prepare()
        player.playWhenReady = false
        player.repeatMode = Player.REPEAT_MODE_ONE

        return player
    }

    /**
     * Release a player back to the pool.
     * Stops and clears media items.
     */
    fun release(player: ExoPlayer) {
        player.stop()
        player.clearMediaItems()

        if (pool.size < maxSize) {
            pool.addLast(player)
        } else {
            player.release()
        }
    }

    /**
     * Preload a video URL into the pool.
     * Creates a temporary ExoPlayer to prepare the media for faster playback.
     * Avoids preloading the same URL multiple times.
     */
    fun preload(videoUrl: String) {
        if (preloadedUrls.contains(videoUrl)) return

        preloadedUrls.add(videoUrl)
        val player = ExoPlayer.Builder(context).build()
        player.setMediaItem(MediaItem.fromUri(videoUrl))
        player.prepare()
        player.playWhenReady = false

        // Release after short delay to keep memory low, media will be cached
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY || state == Player.STATE_ENDED) {
                    player.release()
                }
            }
        })
    }
}