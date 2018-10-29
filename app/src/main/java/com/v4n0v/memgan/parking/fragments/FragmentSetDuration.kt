package com.v4n0v.memgan.parking.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.v4n0v.memgan.parking.R
import com.v4n0v.memgan.parking.mvp.views.TimeDuration
import timber.log.Timber

class FragmentSetDuration : MvpAppCompatFragment(), TimeDuration {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_time_set, container, false)
    }

    override fun setTime() {
        Timber.d("Set time")
    }

}