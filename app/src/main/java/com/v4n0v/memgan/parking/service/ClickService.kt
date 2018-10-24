package com.v4n0v.memgan.parking.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.content.IntentFilter
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.v4n0v.memgan.parking.utils.Helper.ACTION_PARKING_TIME
import com.v4n0v.memgan.parking.utils.Helper.EXTRA_IS_READY_TO_PARK
import timber.log.Timber
// ru.mos.parking.mobile
class ClickService :AccessibilityService(){
    override fun onInterrupt() {
        interrupted = true
        Timber.d( "interrupted")
    }
    companion object {
        private var isWaitingToFinishId: Long? = null
    }
    private var interrupted = false
    private val intentFilter = IntentFilter(ACTION_PARKING_TIME)

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
       Timber.d("onAccessibilityEvent begin")

        val nodeInfo = event?.source ?: return

        val intent = applicationContext.registerReceiver(null, intentFilter)
                ?: return

        Timber.d( "onAccessibilityEvent hasIntent $intent")
        val text = intent.getStringExtra(Intent.EXTRA_TEXT) ?: return

        val itsTime = intent.getBooleanExtra(EXTRA_IS_READY_TO_PARK, false)
        Timber.d(  "onAccessibilityEvent itsTime $itsTime")
        if (!itsTime) return

        var success = false
//        Log.d(TAG, "handleMessage textNodesSize ${textNodes.size}")
//        if (!interrupted && textNodes.any { it.text.toString() == text }) {
        if (!interrupted) {
            val buttonNodes = nodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/send")
            buttonNodes.firstOrNull {
                it.isClickable
            }?.run {
                setSuccess(111L)
                performAction(AccessibilityNodeInfo.ACTION_CLICK)

                success = true
            }

            buttonNodes.forEach { it.recycle() }
        }
//        textNodes.forEach { it.recycle() }
        nodeInfo.recycle()

    }
    private fun setSuccess(id: Long) {
        Timber.d("setSuccess then finally")
        isWaitingToFinishId = id
    }
}