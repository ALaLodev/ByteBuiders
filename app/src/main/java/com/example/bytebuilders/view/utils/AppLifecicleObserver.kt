package com.example.bytebuilders.view.utils

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner

class AppLifecycleObserver : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        // La app entra en segundo plano
        if (MusicPlayer.isPlaying()) {
            MusicPlayer.pause()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        // La app vuelve a primer plano
        if (!MusicPlayer.isPlaying()) {
            MusicPlayer.resume()
        }
    }
}
