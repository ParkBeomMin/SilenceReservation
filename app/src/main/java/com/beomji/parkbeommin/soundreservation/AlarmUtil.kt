package com.beomji.parkbeommin.soundreservation

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

class AlarmUtil(context: Context) {
    private val ALARM_CODE = 1000
    private val alarmManager by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }
    val receiverIntent by lazy { Intent(context, SoundReceiver::class.java) }
    val pendingIntent = PendingIntent.getBroadcast(context, ALARM_CODE, receiverIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)

    fun startAlarm(delay: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pendingIntent)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pendingIntent)
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pendingIntent)
        }
    }

    fun cancelAlarm() {
        alarmManager.cancel(pendingIntent)
    }
}