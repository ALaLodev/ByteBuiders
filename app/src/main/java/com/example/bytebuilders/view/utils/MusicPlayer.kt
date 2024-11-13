package com.example.bytebuilders.view.utils

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri

object MusicPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private var isPausedByFocusLoss = false
    private lateinit var audioManager: AudioManager
    private var isMuted = false
    private var currentVolume = 0.5f // Volumen actual para restaurarlo tras quitar el mute

    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                isPausedByFocusLoss = true
                pause()
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (isPausedByFocusLoss) {
                    resume()
                    isPausedByFocusLoss = false
                }
            }
        }
    }

    fun start(context: Context, musicResId: Int) {
        if (!::audioManager.isInitialized) {
            audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        }

        val result = audioManager.requestAudioFocus(
            audioFocusChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context, musicResId).apply {
                    isLooping = true
                    start()
                }
            } else if (!mediaPlayer!!.isPlaying) {
                mediaPlayer!!.start()
            }
        }
    }

    fun mute() {
        if (!isMuted) {
            currentVolume // Asegúrate de que el volumen sea el máximo antes de mutear
            setVolume(0f) // Silencia la música
            isMuted = true
        } else {
            setVolume(currentVolume) // Restaura el volumen original
            isMuted = false
        }
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun resume() {
        if (mediaPlayer?.isPlaying == false && !isMuted) {
            mediaPlayer?.start()
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        audioManager.abandonAudioFocus(audioFocusChangeListener)
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    fun setVolume(volume: Float) {
        if (mediaPlayer != null) {
            mediaPlayer?.setVolume(volume, volume)
            if (volume > 0) currentVolume = volume
        }
    }

    fun startWithUri(context: Context, audioUri: Uri) {
        release()
        if (!::audioManager.isInitialized) {
            audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        }

        val result = audioManager.requestAudioFocus(
            audioFocusChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, audioUri)
                isLooping = true
                setOnPreparedListener { start() }
                prepareAsync()
            }
        }
    }
}


