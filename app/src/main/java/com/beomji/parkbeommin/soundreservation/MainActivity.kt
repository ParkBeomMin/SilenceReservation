package com.beomji.parkbeommin.soundreservation

import android.content.Context
import android.media.AudioManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.os.Build
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v4.content.ContextCompat
import android.util.Log
import com.aigestudio.wheelpicker.WheelPicker
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import java.util.*
import kotlin.collections.ArrayList
import android.R.attr.delay
import android.app.*
import android.content.DialogInterface
import android.support.v4.app.AlarmManagerCompat.setExact
import android.support.v4.app.AlarmManagerCompat.setExactAndAllowWhileIdle
import android.support.v4.app.NotificationCompat
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var mInterstitialAd: InterstitialAd
    private val REQUEST_CODE = 9999
    private var hourList: ArrayList<Int> = arrayListOf()
    private var minList: ArrayList<Int> = arrayListOf()
    private var selectHour = 0
    private var selectMin = 0
    private val audioManager by lazy {
        getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionCheck()

        adMobInit()

        analyticsInit()

        weelListInit()

        addReservationBtn.setOnClickListener {
            reservationAlarm()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            Log.d("BEOM1", "${data?.getStringExtra("hour")}")
            Log.d("BEOM1", "${data?.getStringExtra("minute")}")
            val array = data?.getStringArrayListExtra("day")
            array?.forEach {
                Log.d("BEOM1", "${it}")
            }
            val calendar = Calendar.getInstance()
        }
    }

    private fun permissionCheck() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted) {
            AlertDialog.Builder(this)
                    .setTitle("권한 확인")
                    .setMessage("방해금지 모드 사용 권한을 허용해주세요. 권한 허용이 안되어 있을 시 사용에 제한이 있을 수 있습니다.")
                    .setPositiveButton("권한 허용하러 가기", DialogInterface.OnClickListener { dialog, which ->
                        val intent = Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                        startActivity(intent)
                    })
                    .setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                    }).show()
        }
    }

    private fun adMobInit() {
        MobileAds.initialize(this, getString(R.string.admob_id))
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = getString(R.string.interstitial_ad_unit_id)
        mInterstitialAd.loadAd(AdRequest.Builder().build())

    }

    private fun analyticsInit() {
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    private fun weelListInit() {
        for (i in 0..24) {
            hourList.add(i)
        }
        for (i in 0..60) {
            minList.add(i)
        }

        hourWp.data = hourList
        minWp.data = minList

        hourWp.isCyclic = true
        minWp.isCyclic = true

        hourWp.setSelectedItemPosition(0)
        minWp.setSelectedItemPosition(0)

        hourWp.setOnItemSelectedListener(WheelPicker.OnItemSelectedListener { picker, data, position ->
            selectHour = data.toString().toInt()
            addReservationBtn.text = "$selectHour 시간 $selectMin 분 후에 소리모드 예약"
        })
        minWp.setOnItemSelectedListener(WheelPicker.OnItemSelectedListener { picker, data, position ->
            selectMin = data.toString().toInt()
            addReservationBtn.text = "$selectHour 시간 $selectMin 분 후에 소리모드 예약"
        })
    }

    private fun reservationAlarm() {
        var reservationTime = selectHour * 60 * 60 * 1000 + selectMin * 60 * 1000
        if (audioManager.ringerMode != AudioManager.RINGER_MODE_SILENT) {
            audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
        }

        val alarmUtil = AlarmUtil(this)
        alarmUtil.cancelAlarm()
        notifyAlarm(this, reservationTime.toLong())
        alarmUtil.startAlarm(reservationTime)
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
                    .setContentText("${dayTime.format(Date(System.currentTimeMillis() + reservationTime))}에 소리모드로 전환됩니다.")

                    .setSmallIcon(R.mipmap.logo)

                    .build()
            (context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(1, notification)
        } else {
            val notification = Notification.Builder(context)
                    .setContentTitle("무음 예약")
                    .setContentText("${dayTime.format(Date(System.currentTimeMillis() + reservationTime))}에 소리모드로 전환됩니다.")

                    .setSmallIcon(R.mipmap.logo)
                    .build()
            (context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(1, notification)
        }
    }

    override fun onBackPressed() {
        mInterstitialAd.show()
        if (!mInterstitialAd.isLoaded) {
            finish()
        } else {
            mInterstitialAd.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                }

                override fun onAdFailedToLoad(errorCode: Int) {
                    // Code to be executed when an ad request fails.
                }

                override fun onAdOpened() {
                    // Code to be executed when the ad is displayed.
                }

                override fun onAdClicked() {
                    // Code to be executed when the user clicks on an ad.
                }

                override fun onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                }

                override fun onAdClosed() {
                    // Code to be executed when the interstitial ad is closed.
                    finish()
                }
            }
        }


    }


}
