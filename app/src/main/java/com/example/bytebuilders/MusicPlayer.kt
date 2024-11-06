package com.example.bytebuilders

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri

//Objeto Singleton para reproducir la música desde cualquier clase
object MusicPlayer {
    private var mediaPlayer: MediaPlayer? = null

    fun start(context: Context, musicResId: Int) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, musicResId).apply {
                isLooping = true
                start()  // Inicia la música en el momento de creación
            }
        } else if (!mediaPlayer!!.isPlaying) {
            mediaPlayer!!.start()
        }
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun resume() {
        mediaPlayer?.start()
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    fun setVolume(volume: Float) {
        mediaPlayer?.setVolume(volume, volume) // Establece volumen min y max
    }

    fun startWithUri(context: Context, audioUri: Uri) {
        release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(context, audioUri)
            isLooping = true
            setOnPreparedListener { start() }
            prepareAsync()
        }
    }
}

