package com.beomji.parkbeommin.soundreservation

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.R.raw
import android.media.MediaPlayer
import android.support.v4.app.NotificationCompat
import android.content.Context.NOTIFICATION_SERVICE
import android.support.v4.content.ContextCompat.getSystemService
import android.app.NotificationManager
import android.app.NotificationChannel
import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.util.Log
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug


class SoundService : Service() {
    private val log = AnkoLogger(this.javaClass)

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()


    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (audioManager.ringerMode == AudioManager.RINGER_MODE_SILENT) {
            audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
        }
        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d("onDestory() 실행", "서비스 파괴")

    }
}
