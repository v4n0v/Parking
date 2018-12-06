package com.v4n0v.memgan.parking.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import com.arellomobile.mvp.MvpAppCompatFragment
import com.v4n0v.memgan.parking.R

class FragmentNoService : MvpAppCompatFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_no_service, container, false)
        val tv = view.findViewById<WebView>(R.id.tvNoServiceMessage)
        val sb= String.format(getString(R.string.noServMessageWeb),getString(R.string.accessibility_service_label), getString(R.string.app_name) )
        tv.loadData(sb, "text/html", "utf-8")
        return view
    }
}