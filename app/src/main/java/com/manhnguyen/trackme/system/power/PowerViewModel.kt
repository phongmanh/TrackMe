package com.manhnguyen.trackme.system.power

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.math.roundToInt

@Singleton
class PowerViewModel @Inject constructor() : ViewModel() {

    var batteryTimeChange = ArrayList<Long>()
    var batteryPctChange = ArrayList<Int>()

    private val batteryLevelChange = MutableLiveData<Intent>()

    fun setBatteryLevelData(intent: Intent) {
        batteryLevelChange.postValue(intent)
    }

    fun getBatteryLevelChange(): LiveData<Intent> {
        return batteryLevelChange
    }


    fun getBatteryEstimation(): String {
        var timeLeft = ""
        try {
            if (batteryTimeChange.size >= 2) {

                val time1: Long = batteryTimeChange[batteryTimeChange.size - 1]
                val time2: Long = batteryTimeChange[batteryTimeChange.size - 2]

                val percent1 = batteryPctChange[batteryPctChange.size - 1]
                val percent2 = batteryPctChange[batteryPctChange.size - 2]

                val percentDiff = percent2 - percent1

                val timeDiffInMillis = time2 - time1
                val timeTakenFor1Percentage =
                    (timeDiffInMillis / percentDiff.toFloat()).roundToInt().toLong()

                val percentage = timeTakenFor1Percentage * percent1
                val hoursLast: Long =
                    abs(TimeUnit.MILLISECONDS.toHours(percentage))
                val minutesLast: Long = abs(
                    TimeUnit.MILLISECONDS.toMinutes(percentage) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(percentage)
                    )
                )
                timeLeft = if (hoursLast > 0) {
                    "$hoursLast hour(s) $minutesLast min(s) left"
                } else {
                    "$minutesLast min(s) left"
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
        return timeLeft
    }
}