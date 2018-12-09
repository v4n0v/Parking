package com.v4n0v.memgan.parking.mvp.views

import android.content.Intent
import com.v4n0v.memgan.parking.activities.LaunchActivity

interface MainView  {
    fun initialiaze()
    fun checkState()
    fun initIntent(clickIntent: Intent, parkIntent: Intent)
    fun startParking()
    fun saveTimePreferences(time: Long)
    fun switchFragment(state: LaunchActivity.State)
    fun showSwitch()
    fun hideSwitch()
    fun markTutorialViewed()

}