package com.v4n0v.memgan.parking.fragments

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.v4n0v.memgan.parking.R
import com.v4n0v.memgan.parking.mvp.views.StartParking
import com.v4n0v.memgan.parking.utils.Helper
import kotlinx.android.synthetic.main.fragment_parking.*


class FragmentStartParking: BaseFragment(), StartParking{

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_parking, container, false)
        val b = view.findViewById<Button>(R.id.btnParking)
        b.setOnClickListener { parkMe() }
        return view
    }
    override fun parkMe() {

        btnParking.setOnClickListener {
            val parkIntent = it.context.packageManager.getLaunchIntentForPackage(Helper.PACKAGE_NAME)
            if (parkIntent != null) {
//                parkIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                parkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                val clickIntent = Intent(Helper.ACTION_PARKING_TIME)
//                clickIntent.putExtra(Helper.EXTRA_IS_READY_TO_PARK, true)
//                clickIntent.putExtra(Helper.EXIT_ID, UUID.randomUUID().toString())
                startActivity(parkIntent)
//                it.context.applicationContext.sendStickyBroadcast(clickIntent)
            } else {
                showInformDialog(getString(R.string.warning), getString(R.string.no_app_message), DialogInterface.OnClickListener { _, _ ->
                    val i = Intent(android.content.Intent.ACTION_VIEW);
                    i.data = Uri.parse("https://play.google.com/store/apps/details?id=${Helper.PACKAGE_NAME}")
                    startActivity(i)
                })
            }

        }

    }

}
