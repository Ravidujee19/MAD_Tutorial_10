package com.example.tute10

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.Intent
import android.util.Log


class MainActivity : AppCompatActivity() {

    lateinit var playPauseButton:ImageView
    private lateinit var skipButton: ImageView
    private lateinit var tvMusicTitle: TextView

    private var isPlaying = false
    private var isBound = false
    private lateinit var musicPlayerService: MusicPlayerService

    private val serviceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicPlayerService.LocalBinder
            musicPlayerService = binder.getService()
            isBound = true
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        playPauseButton = findViewById(R.id.btnPlayPause)
        skipButton = findViewById(R.id.btnSkip)
        tvMusicTitle = findViewById(R.id.tvMusicTitle)

        playPauseButton.setOnClickListener {
            if (!isPlaying){
                musicPlayerService.play()
                tvMusicTitle.text = musicPlayerService.nowPlaying
                isPlaying = true
                playPauseButton.setImageResource(R.drawable.pause)
            }else{
                musicPlayerService.pauseTrack()
                isPlaying = false
                playPauseButton.setImageResource(R.drawable.play)
            }
        }
        skipButton.setOnClickListener {
            musicPlayerService.skipTrack()
            tvMusicTitle.text = musicPlayerService.nowPlaying
        }
    }

    override fun onStart() {
        super.onStart()

        val intent = Intent(this, MusicPlayerService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        Log.d("MainActivity","Service Started")
    }
    override fun onStop() {
        super.onStop()

        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
    }
}