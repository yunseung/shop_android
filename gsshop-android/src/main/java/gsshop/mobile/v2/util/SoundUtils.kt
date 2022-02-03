/*
 * Copyright(C) 2013 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.util

import android.annotation.TargetApi
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.os.Build
import gsshop.mobile.v2.MainApplication
import roboguice.util.Ln

/**
 * 사운드 관련 유틸 모음
 */
object SoundUtils {
    /**
     * for api level 26+
     */
    private lateinit var focusRequest: AudioFocusRequest

    /**
     * for api level under 26
     */
    private lateinit var focusChangeListener: OnAudioFocusChangeListener

    /**
     * request audio focus
     */
	@JvmStatic
	fun requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioManager = MainApplication.getAppContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audioManager.requestAudioFocus(audioFocusRequest)
        } else {
            val audioManager = MainApplication.getAppContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
            //This method was deprecated in API level 26.
            audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN)
        }
    }

    /**
     * Encapsulate information about an audio focus request
     *
     * @return AudioFocusRequest
     */
    @get:TargetApi(Build.VERSION_CODES.O)
    private val audioFocusRequest: AudioFocusRequest
        get() {
            if (!::focusRequest.isInitialized) {
                val playbackAttributes = AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                        .setAudioAttributes(playbackAttributes)
                        .setAcceptsDelayedFocusGain(true)
                        .setOnAudioFocusChangeListener { i -> Ln.i("onAudioFocusChange : $i") }
                        .build()
            }
            return focusRequest
        }

    /**
     * Interface definition for a callback to be invoked when the audio focus of the system is updated
     *
     * @return OnAudioFocusChangeListener
     */
    private val audioFocusChangeListener: OnAudioFocusChangeListener
        get() {
            if (!::focusChangeListener.isInitialized) {
                focusChangeListener = OnAudioFocusChangeListener { focusChange ->
//                    val am = MainApplication.getAppContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    when (focusChange) {
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                        }
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                        }
                        AudioManager.AUDIOFOCUS_LOSS -> {
                        }
                        AudioManager.AUDIOFOCUS_GAIN -> {
                        }
                        else -> {
                        }
                    }
                }
            }
            return focusChangeListener
        }
}