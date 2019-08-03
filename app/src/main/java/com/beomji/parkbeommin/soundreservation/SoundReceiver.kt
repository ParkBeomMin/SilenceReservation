package com.beomji.parkbeommin.soundreservation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug

class SoundReceiver : BroadcastReceiver() {
    private val log = AnkoLogger(this.javaClass)
    override fun onReceive(context: Context?, intent: Intent?) {
        val serviceIntent = Intent(context, SoundService::class.java)
        serviceIntent.putExtra("time", intent?.getStringExtra("time"))
        log.debug { "intent data : ${intent?.getStringExtra("time")}" }
        context?.startService(serviceIntent);
    }

}