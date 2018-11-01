package com.v4n0v.memgan.parking.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.v4n0v.memgan.parking.mvp.views.StartParking

@InjectViewState
class StartParkingPresenter(private val hours:String, val minutes: String) : MvpPresenter<StartParking>() {

    private val hoursList = mutableListOf<String>()
    private val minutesList = mutableListOf<String>()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        init()
    }

    private fun init(){
        for (i in 0 until 13){
            hoursList.add("$i $hours")
        }

        for (i in 0 until 60 step 5){
            minutesList.add("$i $minutes")
        }

        viewState.init(hoursList.toTypedArray(), minutesList.toTypedArray())
    }

    fun parkMe(hoursId: Int, minutesId: Int) {
       viewState.parkMe(hoursList[hoursId], minutesList[minutesId])
    }


}