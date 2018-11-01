package com.v4n0v.memgan.parking.service

import android.R.attr.colorForegroundInverse
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.content.IntentFilter
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.v4n0v.memgan.parking.utils.Helper.ACTION_PARKING_TIME
import com.v4n0v.memgan.parking.utils.Helper.EXIT_ID
import com.v4n0v.memgan.parking.utils.Helper.EXTRA_IS_READY_TO_PARK
import com.v4n0v.memgan.parking.utils.Helper.EXTRA_PAY_BUTTON_CLICKED
import com.v4n0v.memgan.parking.utils.Helper.HOME_PACKAGE_NAME
import com.v4n0v.memgan.parking.utils.Helper.PACKAGE_NAME
import timber.log.Timber
import android.os.Bundle
import com.pawegio.kandroid.clipboardManager
import android.R.attr.data
import android.content.ClipData
import com.v4n0v.memgan.parking.utils.Helper.HOURS_ID
import com.v4n0v.memgan.parking.utils.Helper.MINUTES_ID
import com.v4n0v.memgan.parking.utils.Helper.PARK_PLACE_ID
import java.lang.Thread.sleep


// is clickable android.widget.Button text= ОПЛАТИТЬ ru.mos.parking.mobile:id/parking_park_pay
//is clickable   android.widget.EditText text= Номер парковки / билета ru.mos.parking.mobile:id/parking_parkId
class ParkingService : AccessibilityService() {
    override fun onInterrupt() {
        interrupted = true
        Timber.d("interrupted")
    }

    companion object {
        private var timeToFinishId = ""
    }

    private var interrupted = false
    private val intentFilter = IntentFilter(ACTION_PARKING_TIME)

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        //  Timber.d("onAccessibilityEvent begin")
        val nodeInfo = event?.source ?: return

        Timber.d("onAccessibilityEvent is clickable ${nodeInfo.className} text= ${nodeInfo.text} idRes = ${nodeInfo.viewIdResourceName}")
        val intent = applicationContext.registerReceiver(null, intentFilter)
                ?: return

//        Timber.d("onAccessibilityEvent hasIntent $intent")
//        val text = intent.getStringExtra(Intent.EXTRA_TEXT) ?: return

        val itsTime = intent.getBooleanExtra(EXTRA_IS_READY_TO_PARK, false)
        if (!itsTime) return

        val hours = intent.getStringExtra(HOURS_ID) ?: return
        val minutes = intent.getStringExtra(MINUTES_ID) ?: return
        val parkingNum = intent.getStringExtra(PARK_PLACE_ID) ?: return

        Timber.d("onAccessibilityEvent parking num: $parkingNum\nhours: $hours\nminutes: $minutes")

//        Timber.d("onAccessibilityEvent itsTime $itsTime, time to finish $timeToFinishId")


        val exitId = intent.getStringExtra(EXIT_ID)
        if (exitId.isEmpty())
            return
        val eventType = event.eventType
//         logEvent(nodeInfo)


//        if (nodeInfo.className=="android.widget.NumberPicker"){
//            for (i in 0 until nodeInfo.childCount){
//
//                logEvent(nodeInfo.getChild(i))
//            }
//
//        }
//        when (eventType) {
//            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
//                logEvent(nodeInfo)
//                if (nodeInfo.viewIdResourceName == BUTTON_PAY) {
//                    Timber.d("onAccessibilityEvent pay button clicked")
//                    goBack()
//                }
//            }
//
//            AccessibilityEvent.TYPE_VIEW_FOCUSED -> {
//            }
//        }
        Timber.d("onAccessibilityEvent $timeToFinishId != $exitId")
        if (timeToFinishId != exitId) {
            if (pasteParking(nodeInfo, parkingNum))
                if (handleParkClick(nodeInfo, exitId)) {
                    sleep(500)
                    Timber.d("onAccessibilityEvent job is done, Im awesome!")
//                    val newNodeInfo = event.source ?: return
////                    if (nodeInfo.className=="android.widget.NumberPicker"){
//                        for (i in 0 until newNodeInfo.childCount) {
//                            logEvent(newNodeInfo.getChild(i))
//                        }
////                    }

                }

//            Timber.d("onAccessibilityEvent $timeToFinishId != $exitId")
//            if (listenParkingClick(nodeInfo))
//                Timber.d("onAccessibilityEvent button clicked")
        }

//        if (nodeInfo.className == "android.widget.FrameLayout" && nodeInfo.actionList.size == 4 && nodeInfo.childCount == 1) {
//            //->Linear -> Frame ->Frame   ru.mos.parking.mobile:id/action_bar_root -> Frame android:id/content
////            val n  = nodeInfo.getChild(0).getChild(0).getChild(0).getChild(0).getChild(0)
////             //logEvent(nodeInfo.getChild(0).getChild(0).getChild(0).getChild(0).getChild(0))
////            var i = 0
////            while (i<n.childCount){
////                logEvent(n.getChild(i))
////                i++
//            val myNodes = HashMap<String, AccessibilityNodeInfo>()
//            showTree(nodeInfo, myNodes)
//            for (key in myNodes.keys) {
//                Timber.d("onAccessibilityEvent found node $key")
//            }
//
//            val hoursNode = myNodes["NumberPicker1"]
//            Timber.d("onAccessibilityEvent actions ${hoursNode?.actionList?.size}")
//            scrollNumPickerForResult(hoursNode!!, hours)
////            while (true) {
////                hoursNode?.refresh()
////                Timber.d("onAccessibilityEvent hours${hoursNode?.getChild(1)?.text}")
////                Timber.d("onAccessibilityEvent childs${hoursNode?.childCount}")
////                if (showText(hoursNode!!) == hours)
////                    break
////                hoursNode.performAction(android.view.accessibility.AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
////                showTree(nodeInfo, myNodes)
////                sleep(200)
////            }
//
//        }
//

        interrupted = false
    }


    fun scrollNumPickerForResult(numPicker: AccessibilityNodeInfo, target: String): Boolean {
        val txt = numPicker.getChild(1).text.toString()
        Timber.d("onAccessibilityEvent $txt")
        if (txt!= target) {
            numPicker.performAction(android.view.accessibility.AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
            scrollNumPickerForResult(numPicker, target)
        }
        return true
    }


    private fun showText(numberNode: AccessibilityNodeInfo): String? {
        for (i in 0 until numberNode.childCount) {
            if (numberNode.getChild(i).className == "android.widget.EditText") {
                Timber.d("onAccessibilityEvent EditText index is $i, text is ${numberNode.getChild(i).text}")
                return numberNode.getChild(i).text.toString()
            }
        }
        return null
    }


    private fun showTree(nodeInfo: AccessibilityNodeInfo, nodes: HashMap<String, AccessibilityNodeInfo>) {
//        Timber.d("onAccessibilityEvent\n----------- next relative ----------- parent ${nodeInfo.className}----------")
        for (i in 0 until nodeInfo.childCount) {
            logEvent(nodeInfo.getChild(i))
            if (nodeInfo.getChild(i).className == "android.widget.NumberPicker") { // && nodeInfo.getChild(i).getChild(1).viewIdResourceName == "android:id/numberpicker_input") {
//                Timber.d("onAccessibilityEvent  NumberPicker found!!!!")
                nodes["NumberPicker$i"] = nodeInfo.getChild(i)
//                while (true) {
//                    nodeInfo.getChild(i).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
//                    sleep(200)
//                }
//
            }

            if (nodeInfo.getChild(i).className == "android.widget.Button") {
                if (nodeInfo.getChild(i).viewIdResourceName == "android:id/button2")
                    nodes["CANCEL"] = nodeInfo.getChild(i)
                if (nodeInfo.getChild(i).viewIdResourceName == "android:id/button2")
                    nodes["READY"] = nodeInfo.getChild(i)
            }
        }
        for (i in 0 until nodeInfo.childCount) {
            showTree(nodeInfo.getChild(i), nodes)
        }
    }


    private fun goBack() {
        val parkIntent = packageManager.getLaunchIntentForPackage(HOME_PACKAGE_NAME)
        if (parkIntent != null) {
            parkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            parkIntent.putExtra(EXTRA_PAY_BUTTON_CLICKED, true)
            startActivity(parkIntent)
        }
    }

    private fun logEvent(nodeInfo: AccessibilityNodeInfo) {
        Timber.d("onAccessibilityEvent\nClassName: ${nodeInfo.className} \nText: ${nodeInfo.text} \nViewIdResourceName: ${nodeInfo.viewIdResourceName}\nisClickable: ${nodeInfo.isClickable} \nActions: ${nodeInfo.actionList.size} \n" +
                "Childs: ${nodeInfo.childCount}")
    }

    private fun pasteParking(nodeInfo: AccessibilityNodeInfo, parkingnum: String): Boolean {
        if (!interrupted) {
            val editTextNodes = nodeInfo.findAccessibilityNodeInfosByViewId("ru.mos.parking.mobile:id/parking_parkId")
                    ?: return false
            Timber.d("onAccessibilityEvent edit text found")
            //logEvent(editTextNodes[0])
            editTextNodes.firstOrNull {
                it.isClickable
            }?.run {
                Timber.d("onAccessibilityEvent start paste")
                val arguments = Bundle()
//                val clip = ClipData.newPlainText("park_number", parkingnum)
//                clipboardManager.primaryClip = clip
                arguments.putCharSequence(
                        AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                        parkingnum)
                Timber.d("onAccessibilityEvent refresh")
//                performAction(AccessibilityNodeInfo.ACTION_PASTE)
                performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                Timber.d("onAccessibilityEvent pasteComplete")
                Timber.d("onAccessibilityEvent edittext = ${this.text}")
                sleep(500)
                return true
            }

        }
        return false
    }

    private fun listenParkingClick(nodeInfo: AccessibilityNodeInfo): Boolean {
        if (!interrupted) {
            val buttonNodes = nodeInfo.findAccessibilityNodeInfosByViewId("ru.mos.parking.mobile:id/parking_park_pay")
            Timber.d("onAccessibilityEvent node found")

            buttonNodes.firstOrNull {
                it.isClickable
            }?.run {
                if (this.isSelected) {
                    Timber.d("onAccessibilityEvent button is selected")
                    return true
                }
            }
        }
        return false
    }

    private fun handleParkClick(nodeInfo: AccessibilityNodeInfo, exitId: String): Boolean {
        var success = false
//        Log.d(TAG, "handleMessage textNodesSize ${textNodes.size}")
//        if (!interrupted && textNodes.any { it.text.toString() == text }) {
        if (!interrupted) {
            val count = nodeInfo.childCount
//            Timber.d("onAccessibilityEvent child count is $count")
            var i = 0
            while (i < count) {
                val child = nodeInfo.getChild(i)
//                Timber.d("onAccessibilityEvent child $i is ${child.isContextClickable}")
                i++
            }
//            val list = nodeInfo.for ((i, item) in list.withIndex()) {
//                Timber.d("onAccessibilityEvent label$i = ${item.}")
//                }
            val buttonNodes = nodeInfo.findAccessibilityNodeInfosByViewId("ru.mos.parking.mobile:id/parking_park_pay")
            buttonNodes.firstOrNull {
                it.isClickable
            }?.run {
                setSuccess(exitId)
                performAction(AccessibilityNodeInfo.ACTION_CLICK)
                success = true
            }
//
            buttonNodes.forEach { it.recycle() }
        }
//        textNodes.forEach { it.recycle() }
        nodeInfo.recycle()
        return success
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        val info = AccessibilityServiceInfo()
        info.flags = AccessibilityServiceInfo.DEFAULT or
                AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS or
                AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS

        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        info.packageNames = arrayOf(PACKAGE_NAME)
        serviceInfo = info
    }

    private fun setSuccess(id: String) {
        Timber.d("setSuccess then finally")
        timeToFinishId = id
    }
}