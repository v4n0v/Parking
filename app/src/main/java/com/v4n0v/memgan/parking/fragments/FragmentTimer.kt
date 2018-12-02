package com.v4n0v.memgan.parking.fragments

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.v4n0v.memgan.parking.R
import com.v4n0v.memgan.parking.activities.LaunchActivity
import com.v4n0v.memgan.parking.mvp.presenters.TimerPresenter
import com.v4n0v.memgan.parking.mvp.views.MainView
import com.v4n0v.memgan.parking.mvp.views.TimerView
import com.v4n0v.memgan.parking.utils.Helper.TIMER
import com.v4n0v.memgan.parking.utils.Helper.timerFormat
import kotlinx.android.synthetic.main.fragment_timer.*

class FragmentTimer : BaseFragment(), TimerView {


    lateinit var activity: MainView
    @InjectPresenter
    lateinit var presenter: TimerPresenter

    var timer: CountDownTimer? = null

    @ProvidePresenter
    fun provide(): TimerPresenter {
        return TimerPresenter()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity = context as MainView
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    override fun init() {
        timer = object : CountDownTimer(TIMER.toLong(), 1000) {
            override fun onFinish() {
                activity.startParking()
            }

            override fun onTick(millisUntilFinished: Long) {
                tvTimer.text = timerFormat.format(millisUntilFinished)
                progressBar.progress = 100 - millisUntilFinished.toInt() * 100 / TIMER
            }
        }
        btnCancel.setOnClickListener {
            timer?.cancel()
            activity.switchFragment(LaunchActivity.State.PARKING)
        }

        timer?.start()
    }
}