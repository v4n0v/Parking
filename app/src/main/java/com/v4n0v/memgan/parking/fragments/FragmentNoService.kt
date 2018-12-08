package com.v4n0v.memgan.parking.fragments

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import com.arellomobile.mvp.MvpAppCompatFragment
import com.v4n0v.memgan.parking.R
import android.text.TextUtils



class FragmentNoService : MvpAppCompatFragment() {
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


object TextJustification {

    fun justify(textView: TextView, contentWidth: Float) {
        val text = textView.text.toString()
        val paint = textView.paint

        val lineList = lineBreak(text, paint, contentWidth)

        textView.text = TextUtils.join(" ", lineList).replaceFirst("\\s".toRegex(), "")
    }


    private fun lineBreak(text: String, paint: Paint, contentWidth: Float): ArrayList<String> {
        val wordArray = text.split("\\s".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val lineList = ArrayList<String>()
        var myText = ""

        for (word in wordArray) {
            if (paint.measureText("$myText $word") <= contentWidth)
                myText = "$myText $word"
            else {
                val totalSpacesToInsert = ((contentWidth - paint.measureText(myText)) / paint.measureText(" ")) as Int
                lineList.add(justifyLine(myText, totalSpacesToInsert))
                myText = word
            }
        }
        lineList.add(myText)
        return lineList
    }

    private fun justifyLine(text: String, totalSpacesToInsert: Int): String {
        var totalSpacesToInsert = totalSpacesToInsert
        val wordArray = text.split("\\s".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var toAppend = " "

        while (totalSpacesToInsert >= wordArray.size - 1) {
            toAppend = "$toAppend "
            totalSpacesToInsert = totalSpacesToInsert - (wordArray.size - 1)
        }
        var i = 0
        var justifiedText = ""
        for (word in wordArray) {
            if (i < totalSpacesToInsert)
                justifiedText = "$justifiedText$word $toAppend"
            else
                justifiedText = justifiedText + word + toAppend

            i++
        }

        return justifiedText
    }

}