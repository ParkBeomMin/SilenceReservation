package com.beomji.parkbeommin.soundreservation

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_add_reservation.*
import org.jetbrains.anko.find
import java.util.*
import kotlin.collections.ArrayList

class AddReservation : AppCompatActivity() {
    private var selectDayList: ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reservation)

        timeInit()

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> {
                Log.d("BEOM1", "click!")
                val intent = Intent()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    intent.putExtra("hour", timePicker.hour.toString())
                    intent.putExtra("minute", timePicker.minute.toString())
                }else {
                    intent.putExtra("hour", timePicker.currentHour.toString())
                    intent.putExtra("minute", timePicker.currentMinute.toString())
                }
                intent.putExtra("day", selectDayList)
                setResult(Activity.RESULT_OK, intent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun timeInit() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val min = calendar.get(Calendar.MINUTE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.hour = hour
            timePicker.minute = min
        }else {
            timePicker.currentHour = hour
            timePicker.currentMinute = min
        }
    }

    public fun dayOnClick(view: View) {
        when(view.id) {
            R.id.sunBtn, R.id.monBtn, R.id.tueBtn, R.id.wedBtn, R.id.thuBtn, R.id.friBtn, R.id.satBtn -> {
                val selectBtn = find<Button>(view.id)
                selectBtn.isSelected = !selectBtn.isSelected
                if (selectBtn.isSelected) {
                    selectDayList.add(selectBtn.text.toString())
                }else {
                    selectDayList.remove(selectBtn.text.toString())
                }
            }
        }
    }
}
