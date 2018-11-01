package com.v4n0v.memgan.parking.mvp.views

import com.arellomobile.mvp.MvpView

interface StartParking : MvpView{
    fun init(hours: Array<String>, minutes: Array<String>)
    fun parkMe(hours:String, minutes:String)
}