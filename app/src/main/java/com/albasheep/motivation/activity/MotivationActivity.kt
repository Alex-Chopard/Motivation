package com.albasheep.motivation.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.albasheep.motivation.R
import com.albasheep.motivation.service.EarningsService
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.ThreadMode
import org.greenrobot.eventbus.Subscribe
import com.albasheep.motivation.event.EarningEvent
import kotlinx.android.synthetic.main.activity_motivation.*

class MotivationActivity : AppCompatActivity(), TextWatcher, View.OnClickListener {
    companion object {
        const val SHARED_PREFERENCES_HOURLY_EARNING: String = "shared_preferences_hourly_earning"
        const val SHARED_PREFERENCES: String = "shared_preferences"
        var mHourlyEarning: Float = 0f
    }

    private var mEarningsService: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_motivation)

        val mSharedPreferences:SharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        mHourlyEarning = mSharedPreferences.getFloat(SHARED_PREFERENCES_HOURLY_EARNING, 0f)

        mEarningsService = Intent(this, EarningsService::class.java)

        btn_start.setOnClickListener(this)
        et_hourly_earnings.addTextChangedListener(this)
    }

    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(mEarningsService)
    }


    override fun onClick(v: View?) {
        val vId: Int = v!!.id

        when (vId) {
            R.id.btn_start -> {


                stopService(mEarningsService)
                startService(mEarningsService)
            }
        }
    }

    /*** Edit text event ***/

    override fun afterTextChanged(s: Editable?) {
        val value: String = et_hourly_earnings.text.toString()

        if (value.isNotEmpty()) {
            val money: Float = value.toFloat()

            val mSharedPreferences:SharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
            mSharedPreferences
                    .edit()
                    .putFloat(SHARED_PREFERENCES_HOURLY_EARNING, money)
                    .apply()
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        return
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        return
    }

    /*** END Edit text event ***/

    /*** EVENT BUS ***/

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: EarningEvent) {
        tv_earnings.text = "${event.mValue} €"
    }

    /*** END EVENT BUS ***/
}
