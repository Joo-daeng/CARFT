package com.hyundai.carlego.infrastructure.pleos

import ai.pleos.playground.tts.TextToSpeech
import ai.pleos.playground.tts.constant.ErrorType
import ai.pleos.playground.tts.constant.Mode
import ai.pleos.playground.tts.listener.OnInitializeListener
import android.content.Context
import android.util.Log
import java.util.Locale

class PleosSpeaker(context: Context) {
    private val tts = TextToSpeech(context.applicationContext, Mode.ON_DEVICE)
    private var ready = false
    private var pending: String? = null

    fun start() {
        tts.initialize(object : OnInitializeListener {
            override fun onSuccess() {
                tts.setLocale(Locale.KOREA)
                ready = true
                pending?.let { speak(it) }
                pending = null
            }

            override fun onFailure(errorType: ErrorType) {
                Log.w(TAG, "tts init failed: $errorType")
            }
        })
    }

    fun speak(text: String) {
        if (!ready) {
            pending = text
            return
        }
        tts.speak(text, true)
    }

    fun stop() {
        tts.release()
        ready = false
    }

    companion object {
        private const val TAG = "CarLego.Tts"
    }
}
