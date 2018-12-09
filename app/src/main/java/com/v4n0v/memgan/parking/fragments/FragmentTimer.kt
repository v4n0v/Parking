package com.v4n0v.memgan.parking.fragments

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationSet
import com.v4n0v.memgan.parking.R
import com.v4n0v.memgan.parking.activities.LaunchActivity
import com.v4n0v.memgan.parking.activities.PREFS_TIME
import com.v4n0v.memgan.parking.mvp.views.MainView
import com.v4n0v.memgan.parking.mvp.views.TimerView
import com.v4n0v.memgan.parking.utils.Animator
import com.v4n0v.memgan.parking.utils.Animator.Companion.zoomInAnimation
import com.v4n0v.memgan.parking.utils.Helper
import com.v4n0v.memgan.parking.utils.Helper.timerFormat
import kotlinx.android.synthetic.main.fragment_timer.*

class FragmentTimer : BaseFragment(), TimerView {

    lateinit var activity: MainView
    private var timer: CountDownTimer? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity = context as MainView
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    override fun init() {
        val prefs = context?.getSharedPreferences(PREFS_TIME, MODE_PRIVATE)
        val taskTime = (prefs?.getLong("time", Helper.TIMER)) ?:Helper.TIMER

        timer = object : CountDownTimer(taskTime, 1000) {
            override fun onFinish() {
                activity.startParking()
            }

            override fun onTick(millisUntilFinished: Long) {
                tvTimer.text = timerFormat.format(millisUntilFinished)
                progressBar.progress = 100 - millisUntilFinished.toInt() * 100 / taskTime.toInt()
            }
        }
        fabCancel.setOnClickListener {
            timer?.cancel()
            activity.switchFragment(LaunchActivity.State.PARKING)
        }
        fabCancel.startAnimation(zoomInAnimation())
        timer?.start()
    }
}