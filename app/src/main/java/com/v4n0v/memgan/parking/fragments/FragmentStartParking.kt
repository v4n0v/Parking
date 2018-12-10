package com.v4n0v.memgan.parking.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationSet
import com.v4n0v.memgan.parking.R
import com.v4n0v.memgan.parking.activities.LaunchActivity
import com.v4n0v.memgan.parking.activities.LaunchActivity.Companion.SETTINGS_TIME
import com.v4n0v.memgan.parking.activities.PREFS_TIME
import com.v4n0v.memgan.parking.mvp.views.MainView
import com.v4n0v.memgan.parking.mvp.views.StartParking
import com.v4n0v.memgan.parking.utils.Animator
import com.v4n0v.memgan.parking.utils.Helper
import kotlinx.android.synthetic.main.fragment_parking.*
import java.util.*


class FragmentStartParking : BaseFragment(), StartParking {



    lateinit var activity: MainView

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity = context as MainView
    }

    private fun init(){
        for (i in 0 until 13)
            hoursList.add("$i ${getString(R.string.hours)}")
        for (i in 0 until 60 step 5)
            minutesList.add("$i ${getString(R.string.minutes)}")

        init(hoursList.toTypedArray(), minutesList.toTypedArray())
    }

    fun parkMe(hoursId: Int, minutesId: Int) {
        parkMe(hoursList[hoursId], minutesList[minutesId])
    }

    private val hoursList = mutableListOf<String>()
    private val minutesList = mutableListOf<String>()
    @SuppressLint("SetTextI18n")
    override fun init(hours: Array<String>, minutes: Array<String>) {
        hoursPicker.wrapSelectorWheel = true
        hoursPicker.displayedValues = hours
        minutesPicker.displayedValues = minutes
        hoursPicker.minValue = 0
        hoursPicker.maxValue = hours.size - 1

        minutesPicker.minValue = 0
        minutesPicker.maxValue = minutes.size - 1

        val prefs = context?.getSharedPreferences(PREFS_TIME, MODE_PRIVATE)
        tvTimeCount.text = "Парковка через: ${Helper.timerFormat.format(prefs?.getLong(SETTINGS_TIME, Helper.TIMER))}"
//        //todo УДОЛИ
//        etParkingPlace.setText("4444")
//        hoursPicker.value = 6
//        minutesPicker.value = 6

        fab.setOnClickListener {
            if (etParkingPlace.text.isEmpty() || etParkingPlace.text.toString().length != 4 ) {
                toast(getString(R.string.empty_perking))
                return@setOnClickListener
            }

            if ( minutesPicker.value == 0 && hoursPicker.value==0){
                toast(getString(R.string.empty_time))
                return@setOnClickListener
            }
          parkMe(hoursPicker.value, minutesPicker.value)
        }

        btnPark.setOnClickListener {
            if (etParkingPlace.text.isEmpty() || etParkingPlace.text.toString().length != 4 ) {
                toast(getString(R.string.empty_perking))
                return@setOnClickListener
            }

            if ( minutesPicker.value == 0 && hoursPicker.value==0){
                toast(getString(R.string.empty_time))
                return@setOnClickListener
            }
           parkMe(hoursPicker.value, minutesPicker.value)
        }
        btnPark.startAnimation(Animator.zoomInAnimation())
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_parking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
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
            showCacelableInformDialog(getString(R.string.warning), getString(R.string.no_app_message), DialogInterface.OnClickListener { _, _ ->
                val i = Intent(android.content.Intent.ACTION_VIEW);
                i.data = Uri.parse("https://play.google.com/store/apps/details?id=${Helper.PACKAGE_NAME}")
                startActivity(i)
            })
        }
    }

}
