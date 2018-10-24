package com.v4n0v.memgan.parking.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.content.IntentFilter
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.v4n0v.memgan.parking.utils.Helper.ACTION_PARKING_TIME
import com.v4n0v.memgan.parking.utils.Helper.EXTRA_IS_READY_TO_PARK
import com.v4n0v.memgan.parking.utils.Helper.PACKAGE_NAME
import timber.log.Timber


// is clickable android.widget.Button text= ОПЛАТИТЬ ru.mos.parking.mobile:id/parking_park_pay
class ClickService : AccessibilityService() {
    override fun onInterrupt() {
        interrupted = true
        Timber.d("interrupted")
    }

    companion object {
        private var isWaitingToFinishId: Long? = null
    }

    private var interrupted = false
    private val intentFilter = IntentFilter(ACTION_PARKING_TIME)

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Timber.d("onAccessibilityEvent begin")

        val nodeInfo = event?.source ?: return

        Timber.d("onAccessibilityEvent is clickable ${ nodeInfo.className} text= ${nodeInfo.text} ${nodeInfo.viewIdResourceName}")
        val intent = applicationContext.registerReceiver(null, intentFilter)
                ?: return

        Timber.d("onAccessibilityEvent hasIntent $intent")
//        val text = intent.getStringExtra(Intent.EXTRA_TEXT) ?: return

        val itsTime = intent.getBooleanExtra(EXTRA_IS_READY_TO_PARK, false)
        Timber.d("onAccessibilityEvent itsTime $itsTime")
        if (!itsTime) return

        var success = false
//        Log.d(TAG, "handleMessage textNodesSize ${textNodes.size}")
//        if (!interrupted && textNodes.any { it.text.toString() == text }) {
        if (!interrupted) {
            val count = nodeInfo.childCount
            Timber.d("onAccessibilityEvent child count is $count")
            var i = 0
            while (i< count){
                val child = nodeInfo.getChild(i)
                Timber.d("onAccessibilityEvent child $i is ${child.isContextClickable}")
                i++
            }
//            val list = nodeInfo.for ((i, item) in list.withIndex()) {
//                Timber.d("onAccessibilityEvent label$i = ${item.}")
//                }
//            val buttonNodes = nodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/send")
//            buttonNodes.firstOrNull {
//                it.isClickable
//            }?.run {
//                setSuccess(111L)
//                performAction(AccessibilityNodeInfo.ACTION_CLICK)
//
//                success = true
//            }
//
//            buttonNodes.forEach { it.recycle() }
                }
//        textNodes.forEach { it.recycle() }
                nodeInfo.recycle()

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

                        private fun setSuccess(id: Long) {
                    Timber.d("setSuccess then finally")
                    isWaitingToFinishId = id
                }
                }