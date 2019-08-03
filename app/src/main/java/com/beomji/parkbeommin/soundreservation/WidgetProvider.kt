package com.beomji.parkbeommin.soundreservation

import android.app.*
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v4.content.ContextCompat.startForegroundService
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*


class WidgetProvider : AppWidgetProvider() {
    private val HALF_HOUR_MODE = "android.action.HALF_HOUR_MODE"
    private val HOUR_MODE = "android.action.HOUR_MODE"
    private val TWO_HOUR_MODE = "android.action.TWO_HOUR_MODE"
    private val FOUR_HOUR_MODE = "android.action.FOUR_HOUR_MODE"
    private val INTENT_KEY = "time"
    private val HALF_HOUR = 1800000
    private val HOUR = HALF_HOUR * 2
    private val TWO_HOUR = HOUR * 2
    private val FOUR_HOUR = HOUR * 4

    //    위젯 갱신 주기에 따라 위젯을 갱신할때 호출
    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        appWidgetIds?.forEach { appWidgetId ->
            val views: RemoteViews = addViews(context)
            appWidgetManager?.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        val audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val receiverIntent = Intent(context, SoundReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, receiverIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var action = intent?.action
        Log.d("BEOM1", "action : $action")
        var reservationTime = System.currentTimeMillis() + intent?.getIntExtra(INTENT_KEY, 0)!!

        when (action) {
            HALF_HOUR_MODE -> {
                if (audioManager.ringerMode != AudioManager.RINGER_MODE_SILENT) {
                    audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                }
                notifyAlarm(context, reservationTime)
                startAlarm(alarmManager, pendingIntent, HALF_HOUR)
            }
            HOUR_MODE -> {
                if (audioManager.ringerMode != AudioManager.RINGER_MODE_SILENT) {
                    audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                }
                notifyAlarm(context, reservationTime)
                startAlarm(alarmManager, pendingIntent, HOUR)
            }
            TWO_HOUR_MODE -> {
                if (audioManager.ringerMode != AudioManager.RINGER_MODE_SILENT) {
                    audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                }
                notifyAlarm(context, reservationTime)
                startAlarm(alarmManager, pendingIntent, TWO_HOUR)
            }
            FOUR_HOUR_MODE -> {
                if (audioManager.ringerMode != AudioManager.RINGER_MODE_SILENT) {
                    audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                }
                notifyAlarm(context, reservationTime)
                startAlarm(alarmManager, pendingIntent, FOUR_HOUR)
            }
        }
    }

    //    위젯이 처음 생성될때 호출되며, 동일한 위젯의 경우 처음 호출
    override fun onEnabled(context: Context?) {
        super.onEnabled(context)

    }

    //    위젯의 마지막 인스턴스가 제거될때 호출
    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
    }

    //    위젯이 사용자에 의해 제거될때 호출
    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
    }

    private fun setMode(context: Context?, mode: String): PendingIntent {
//        val intent = Intent("SOUND_MODE")
//        https://stackoverflow.com/questions/21715168/widget-setonclickpendingintent 참고
        val intent = Intent(context, WidgetProvider::class.java)
        intent.action = mode
        when (mode) {
            HALF_HOUR_MODE -> {
                intent.putExtra(INTENT_KEY, HALF_HOUR)
            }
            HOUR_MODE -> {
                intent.putExtra(INTENT_KEY, HOUR)
            }
            TWO_HOUR_MODE -> {
                intent.putExtra(INTENT_KEY, TWO_HOUR)
            }
            FOUR_HOUR_MODE -> {
            intent.putExtra(INTENT_KEY, FOUR_HOUR)
        }

        }

        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun addViews(context: Context?): RemoteViews {
        val views = RemoteViews(context?.packageName, R.layout.widget)
        views.setOnClickPendingIntent(R.id.halfReservationBtn, setMode(context, HALF_HOUR_MODE))
        views.setOnClickPendingIntent(R.id.hourReservationBtn, setMode(context, HOUR_MODE))
        views.setOnClickPendingIntent(R.id.twoHourReservationBtn, setMode(context, TWO_HOUR_MODE))
        views.setOnClickPendingIntent(R.id.fourHourReservationBtn, setMode(context, FOUR_HOUR_MODE))
        return views
    }

    private fun notifyAlarm(context: Context?, reservationTime: Long) {
        val dayTime = SimpleDateFormat("kk시mm분")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val CHANNEL_ID = "default"
            val channel = NotificationChannel(CHANNEL_ID,
                    "silent reservation",
                    NotificationManager.IMPORTANCE_DEFAULT)

            (context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("무음 예약")
                    .setContentText("${dayTime.format(Date(reservationTime))}에 소리모드로 전환됩니다.")
                    .setSmallIcon(R.mipmap.logo)

                    .build()
            (context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(1, notification)
        }else {
            val notification = Notification.Builder(context)
                    .setContentTitle("무음 예약")
                    .setContentText("${dayTime.format(Date(System.currentTimeMillis() + reservationTime))}에 소리모드로 전환됩니다.")
                    .setSmallIcon(R.mipmap.logo)
                    .build()
            (context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(1, notification)
        }
    }

    private fun startAlarm( alarmManager: AlarmManager, pendingIntent: PendingIntent, delay: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pendingIntent)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pendingIntent)
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pendingIntent)
        }
    }

}

