package com.v4n0v.memgan.parking.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.v4n0v.memgan.parking.mvp.views.TimerView

@InjectViewState
class TimerPresenter:MvpPresenter<TimerView>(){

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
    }

}