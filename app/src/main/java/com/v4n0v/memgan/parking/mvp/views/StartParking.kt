package com.v4n0v.memgan.parking.mvp.views

interface StartParking {
    fun init(hours: Array<String>, minutes: Array<String>)
    fun parkMe(hours:String, minutes:String)
}