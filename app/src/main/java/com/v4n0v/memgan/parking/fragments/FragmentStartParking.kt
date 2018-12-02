package com.v4n0v.memgan.parking.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.v4n0v.memgan.parking.R
import com.v4n0v.memgan.parking.activities.LaunchActivity
import com.v4n0v.memgan.parking.activities.PREFS_TIME
import com.v4n0v.memgan.parking.mvp.presenters.StartParkingPresenter
import com.v4n0v.memgan.parking.mvp.views.MainView
import com.v4n0v.memgan.parking.mvp.views.StartParking
import com.v4n0v.memgan.parking.utils.Helper
import kotlinx.android.synthetic.main.fragment_parking.*
import java.util.*


class FragmentStartParking : BaseFragment(), StartParking {

    @InjectPresenter
    lateinit var presenter: StartParkingPresenter

    @ProvidePresenter
    fun provide(): StartParkingPresenter {
        return StartParkingPresenter(getString(R.string.hours), getString(R.string.minutes))
    }

    lateinit var activity: MainView


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity = context as MainView
    }

    @SuppressLint("SetTextI18n")
    override fun init(hours: Array<String>, minutes: Array<String>) {
        hoursPicker.wrapSelectorWheel = true
        hoursPicker.displayedValues = hours
        minutesPicker.displayedValues = minutes
        hoursPicker.minValue = 0
        hoursPicker.maxValue = hours.size - 1

        minutesPicker.minValue = 0
        minutesPicker.maxValue = minutes.size - 1

        val prefs = context?.getSharedPreferences(PREFS_TIME, MvpAppCompatActivity.MODE_PRIVATE)
        val time = prefs?.getInt("time", Helper.TIMER)
        tvTimeCount.text = "Парковка через: ${Helper.timerFormat.format(time?.toLong())}"
        //todo УДОЛИ
        etParkingPlace.setText("4444")
        hoursPicker.value = 6
        minutesPicker.value = 6

        btnParking.setOnClickListener {
            if (etParkingPlace.text.isEmpty() || etParkingPlace.text.toString().length != 4) {
                toast(getString(R.string.empty_perking))
                return@setOnClickListener
            }
            presenter.parkMe(hoursPicker.value, minutesPicker.value)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_parking, container, false)
    }

    override fun parkMe(hours: String, minutes: String) {
        val parkIntent = context?.packageManager?.getLaunchIntentForPackage(Helper.PACKAGE_NAME)
        if (parkIntent != null) {
            parkIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            parkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            val clickIntent = Intent(Helper.ACTION_PARKING_TIME)
            clickIntent.putExtra(Helper.EXTRA_IS_READY_TO_PARK, true)
            clickIntent.putExtra(Helper.EXIT_ID, UUID.randomUUID().toString())
            clickIntent.putExtra(Helper.EXIT_HOURS_ID, UUID.randomUUID().toString())
            clickIntent.putExtra(Helper.PARK_PLACE_ID, etParkingPlace.text.toString())
            clickIntent.putExtra(Helper.MINUTES_ID, minutes)
            clickIntent.putExtra(Helper.HOURS_ID, hours)

            activity.initIntent(clickIntent, parkIntent)
            activity.switchFragment(LaunchActivity.State.TIMER)
        } else {
            showInformDialog(getString(R.string.warning), getString(R.string.no_app_message), DialogInterface.OnClickListener { _, _ ->
                val i = Intent(android.content.Intent.ACTION_VIEW);
                i.data = Uri.parse("https://play.google.com/store/apps/details?id=${Helper.PACKAGE_NAME}")
                startActivity(i)
            })
        }
    }

}
