package com.v4n0v.memgan.parking.fragments

import android.graphics.Paint
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.v4n0v.memgan.parking.R


class FragmentNoService : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_no_service, container, false)
        val tv = view.findViewById<TextView>(R.id.tvNoServiceMessage)

        tv.text = String.format(getString(R.string.noServMessageStart),getString(R.string.accessibility_service_label), getString(R.string.app_name) )

//        val tv = view.findViewById<WebView>(R.id.tvNoServiceMessage)
//        val sb= String.format(getString(R.string.noServMessageWeb),getString(R.string.accessibility_service_label), getString(R.string.app_name) )
//        tv.loadData(sb, "text/html", "utf-8")
        return view
    }
}
