package com.v4n0v.memgan.parking.mvp.views

import android.content.Intent
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.v4n0v.memgan.parking.activities.LaunchActivity

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface MainView : MvpView {
    fun initialiaze()
    fun checkState()
    fun initIntent(clickIntent: Intent, parkIntent: Intent)
    fun startParking()
    fun switchFragment(state: LaunchActivity.State)
}