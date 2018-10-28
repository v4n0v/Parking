package com.v4n0v.memgan.parking.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.v4n0v.memgan.parking.utils.Helper.ACTION_PARKING_TIME
import com.v4n0v.memgan.parking.utils.Helper.BUTTON_PAY
import com.v4n0v.memgan.parking.utils.Helper.EXIT_ID
import com.v4n0v.memgan.parking.utils.Helper.EXTRA_IS_READY_TO_PARK
import com.v4n0v.memgan.parking.utils.Helper.EXTRA_PAY_BUTTON_CLICKED
import com.v4n0v.memgan.parking.utils.Helper.HOME_PACKAGE_NAME
import com.v4n0v.memgan.parking.utils.Helper.PACKAGE_NAME
import timber.log.Timber
import java.util.*


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
        Timber.d("onAccessibilityEvent begin")
        val nodeInfo = event?.source ?: return

        Timber.d("onAccessibilityEvent is clickable ${nodeInfo.className} text= ${nodeInfo.text} ${nodeInfo.viewIdResourceName}")
        val intent = applicationContext.registerReceiver(null, intentFilter)
                ?: return

        Timber.d("onAccessibilityEvent hasIntent $intent")
//        val text = intent.getStringExtra(Intent.EXTRA_TEXT) ?: return

        val itsTime = intent.getBooleanExtra(EXTRA_IS_READY_TO_PARK, false)
        Timber.d("onAccessibilityEvent itsTime $itsTime, time to finish $timeToFinishId")
        if (!itsTime) return

        val exitId = intent.getStringExtra(EXIT_ID)
        if (exitId.isEmpty())
            return
        val eventType = event.eventType
        when (eventType) {
            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                logEvent(nodeInfo)
                if (nodeInfo.viewIdResourceName == BUTTON_PAY) {
                    Timber.d("onAccessibilityEvent pay button clicked")
                    goBack()
                }
            }

            AccessibilityEvent.TYPE_VIEW_FOCUSED -> {
            }

        }

        if (timeToFinishId != exitId) {
            Timber.d("onAccessibilityEvent $timeToFinishId != $exitId")
            if (listenParkingClick(nodeInfo))
                Timber.d("onAccessibilityEvent button clicked")
        }
//            if (handleParkClick(nodeInfo, exitId))
//                Timber.d("onAccessibilityEvent job is done, Im awesome!")

        interrupted = false
    }

    private fun goBack(){
        val parkIntent = packageManager.getLaunchIntentForPackage(HOME_PACKAGE_NAME)
        if (parkIntent != null) {
            parkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            parkIntent.putExtra(EXTRA_PAY_BUTTON_CLICKED, true)
            startActivity(parkIntent)
        }
    }

    private fun logEvent(nodeInfo: AccessibilityNodeInfo) {
        Timber.d("onAccessibilityEvent\nClassName: ${nodeInfo.className} \nText: ${nodeInfo.text} \nViewIdResourceName: ${nodeInfo.viewIdResourceName}\nisClickable: ${nodeInfo.isClickable}")
    }

    override fun onKeyEvent(event: KeyEvent?): Boolean {
//        return super.onKeyEvent(event)

        val action = event?.action
        Timber.d("onKeyEvent action = ${action.toString()}")
        event?.source

        return false
    }

    private fun listenParkingClick(nodeInfo: AccessibilityNodeInfo): Boolean {
        if (!interrupted) {
            val buttonNodes = nodeInfo.findAccessibilityNodeInfosByViewId("ru.mos.parking.mobile:id/parking_park_pay")
            Timber.d("onAccessibilityEvent button found")

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
            Timber.d("onAccessibilityEvent child count is $count")
            var i = 0
            while (i < count) {
                val child = nodeInfo.getChild(i)
                Timber.d("onAccessibilityEvent child $i is ${child.isContextClickable}")
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