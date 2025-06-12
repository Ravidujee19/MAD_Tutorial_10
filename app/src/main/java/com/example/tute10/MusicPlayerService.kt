package com.example.tute10

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import java.io.IOException
import androidx.core.net.toUri

class MusicPlayerService : Service(), MediaPlayer.OnPreparedListener {

    inner class LocalBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }

    private lateinit var mediaPlayer: MediaPlayer
    private var currentTrack = 0
    private lateinit var trackList: List<Int>
    private var isPaused = false
    var nowPlaying = ""

    private var isInitialized = false

    override fun onBind(intent: Intent): IBinder {
        return LocalBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
            when (action) {
                "ACTION_PLAY" -> playTrack(currentTrack)
                "ACTION_PAUSE" -> pauseTrack()
                "ACTION_SKIP" -> skipTrack()
                "ACTION_STOP" -> {
                    stopTrack()
                    stopSelf()
                }
            }
        }
        return START_NOT_STICKY
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mp?.start()
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        mediaPlayer.setOnPreparedListener(this)
        trackList = listOf(R.raw.track1, R.raw.track2, R.raw.track3, R.raw.track4)
        isInitialized = true
    }

    private fun playTrack(trackIndex: Int) {
        if (!isInitialized) return

        val uri = "android.resource://$packageName/${trackList[trackIndex]}".toUri()
        nowPlaying = "Now Playing: Track ${trackIndex + 1}"

        if (isPaused) {
            mediaPlayer.start()
            isPaused = false
        } else {
            try {
                mediaPlayer.reset()
                mediaPlayer.setDataSource(this, uri)
                mediaPlayer.prepareAsync()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun play() {
        if (!isInitialized) return
        playTrack(currentTrack)
    }

    fun pauseTrack() {
        if (isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            isPaused = true
        }
    }

    fun skipTrack() {
        if (!isInitialized) return
        currentTrack = (currentTrack + 1) % trackList.size
        playTrack(currentTrack)
    }

    fun stopTrack() {
        if (isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.release()
            mediaPlayer = MediaPlayer()
            isInitialized = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isInitialized) {
            mediaPlayer.release()
        }
    }
}
