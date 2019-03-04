package com.albasheep.motivation.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler
import android.os.IBinder
import com.albasheep.motivation.activity.MotivationActivity
import android.util.Log
import com.albasheep.motivation.event.EarningEvent
import org.greenrobot.eventbus.EventBus


class EarningsService: Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private val TAG: String = "[EarningsService]"
    private val mInterval: Float = 3600000f
    private var mHourlyEarning: Float = 0f
    private val mHandler: Handler = Handler()
    private val mRefresh: Long = 10 // Refresh each second !

    private var mCurrentMoney: Float = 0f

    override fun onCreate() {
        Log.i(TAG, "On Create")
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(MotivationActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE)

        this.mHourlyEarning = sharedPreferences.getFloat(MotivationActivity.SHARED_PREFERENCES_HOURLY_EARNING, 0f)

        object : Runnable {
            override fun run() {
                try {
                    mCurrentMoney += (mRefresh * mHourlyEarning) / mInterval

                    Log.i(TAG, "gain : " + mCurrentMoney)

                    EventBus.getDefault().post(EarningEvent(mCurrentMoney))

                    mHandler.postDelayed(this, mRefresh)
                } catch (ex: Exception) {
                    Log.i(TAG, "error - " + ex.localizedMessage)
                }

            }
        }.run()
    }
}