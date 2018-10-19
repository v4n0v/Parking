package com.example.v4n0v.parking.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.v4n0v.parking.mvp.views.MainView
import timber.log.Timber

@InjectViewState
class MainPresenter : MvpPresenter<MainView>() {


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        initialize()
        viewState.initialiaze()
    }

    fun initialize() {
        Timber.d("MainPresenter init")
    }

}
