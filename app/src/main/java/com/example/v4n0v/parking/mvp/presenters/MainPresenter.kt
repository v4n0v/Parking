package com.example.v4n0v.parking.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.v4n0v.parking.mvp.views.MainView
import io.reactivex.Scheduler

@InjectViewState
class MainPresenter(val scheduler: Scheduler) : MvpPresenter<MainView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        initialize()
        viewState.initialiaze()
    }

    fun initialize(){

    }

}
