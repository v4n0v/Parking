package com.v4n0v.memgan.parking.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
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
import android.view.accessibility.AccessibilityNodeInfo.ACTION_SCROLL_FORWARD
import com.v4n0v.memgan.parking.utils.Helper.EXIT_HOURS_ID
import com.v4n0v.memgan.parking.utils.Helper.HOURS_ID
import com.v4n0v.memgan.parking.utils.Helper.MINUTES_ID
import com.v4n0v.memgan.parking.utils.Helper.PARK_PLACE_ID
import java.lang.Thread.sleep

class ParkingService : AccessibilityService() {
    override fun onInterrupt() {
        interrupted = true
        Timber.d("interrupted")
    }

    companion object {
        private var finishPayId = ""
        private var finishHoursId = ""
        private var finishMinutesId = ""
        private var finihExitDialogId = ""
        private var FRAME_ID = "custom"

        private var PARKING_ID = "ru.mos.parking.mobile:id/parking_parkId"
        private var BUTTON_BAR_ID = "ru.mos.parking.mobile:id/buttonPanel"
        private var NUMBER_PICKERS_ID = "ru.mos.parking.mobile:id/custom"
        private var PAY_ID = "ru.mos.parking.mobile:id/parking_park_pay"
        private var NUMBER_PICKER_WIDGET = "NumberPicker"
        private var BUTON_WIDGET = "Button"

    }

    private var interrupted = false
    private val intentFilter = IntentFilter(ACTION_PARKING_TIME)

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val nodeInfo = event?.source ?: return

        val intent = applicationContext.registerReceiver(null, intentFilter)
                ?: return

        val itsTime = intent.getBooleanExtra(EXTRA_IS_READY_TO_PARK, false)
        if (!itsTime) return

        val hours = intent.getStringExtra(HOURS_ID) ?: return
        val minutes = intent.getStringExtra(MINUTES_ID) ?: return
        val parkingNum = intent.getStringExtra(PARK_PLACE_ID) ?: return

        val exitDialogId = intent.getStringExtra(EXIT_HOURS_ID)
        if (exitDialogId.isEmpty())
            return

        val exitId = intent.getStringExtra(EXIT_ID)
        if (exitId.isEmpty())
            return

        val exitHoursId = intent.getStringExtra(EXIT_ID)
        if (exitHoursId.isEmpty())
            return
        val exitMinutesId = intent.getStringExtra(EXIT_ID)
        if (exitMinutesId.isEmpty())
            return

        if (finishPayId != exitId)
            if (pasteParking(nodeInfo, parkingNum))
                if (handleParkClick(nodeInfo, exitId))
                    setSuccess(exitId)


        val myNodes = HashMap<String, AccessibilityNodeInfo>()
        val dialog = getViewsById(NUMBER_PICKERS_ID, NUMBER_PICKER_WIDGET, rootInActiveWindow, myNodes)
        val buttonBar = getViewsById(BUTTON_BAR_ID, BUTON_WIDGET, rootInActiveWindow, myNodes)
        if (myNodes.size > 0) {
            // logViewHierarchy(rootInActiveWindow, 0)
            for (key in myNodes.keys)
                Timber.d("onAccessibilityEvent found node $key")
            val hoursNode = myNodes["NumberPicker1"] ?: return
            val minutesNode = myNodes["NumberPicker2"] ?: return
            val doneButtonNode = myNodes["Button1"] ?: return

            if (finishHoursId != exitHoursId)
                handleNumberPicker(hoursNode, hours) {
                    finishHoursId = exitHoursId
                }
            if (finishMinutesId != exitMinutesId)
                handleNumberPicker(minutesNode, minutes) {
                    finishMinutesId = exitMinutesId
                }
            if (finishMinutesId == exitMinutesId && finishHoursId == exitHoursId && finihExitDialogId != exitHoursId)
                handleButtonDone(doneButtonNode) {
                    finihExitDialogId = exitMinutesId
                    dialog?.forEach { it.recycle() }
                    buttonBar?.forEach { it.recycle() }
                }
        }
        interrupted = false
    }


    private fun handleButtonDone(node: AccessibilityNodeInfo, action: () -> Unit) {
        if (node.isClickable) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            action()
        }
    }

    private fun handleNumberPicker(node: AccessibilityNodeInfo, target: String, action: () -> Unit) {
        val hoursPickerText = node.getChild(1)?.text.toString()
        if (hoursPickerText != target) {
            node.performAction(ACTION_SCROLL_FORWARD)
            sleep(50)
        } else {
            action()
        }
    }

    private fun logViewHierarchy(nodeInfo: AccessibilityNodeInfo?, depth: Int) {
        if (nodeInfo == null) return
        var spacerString = ""
        for (i in 0 until depth) {
            spacerString += '-'.toString()
        }

        for (i in 0 until nodeInfo.childCount) {
            logViewHierarchy(nodeInfo.getChild(i), depth + 1)
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

    private fun getViewsById(id: String, wiget: String, nodeInfo: AccessibilityNodeInfo?, nodes: HashMap<String, AccessibilityNodeInfo>): List<AccessibilityNodeInfo>? {
        nodeInfo ?: return null
        if (!interrupted) {
            val dialgNodes = nodeInfo.findAccessibilityNodeInfosByViewId(id)

            if (dialgNodes != null)
                if (dialgNodes.size != 0) {
                    initView(wiget, dialgNodes[0], 0, nodes)
                    return dialgNodes
                }
        }
        return null
    }

    private fun pasteParking(nodeInfo: AccessibilityNodeInfo, parkingnum: String): Boolean {
        var success = false
        if (!interrupted) {
            val editTextNodes = nodeInfo.findAccessibilityNodeInfosByViewId(PARKING_ID)
                    ?: return false
            Timber.d("onAccessibilityEvent edit text found")
            editTextNodes.firstOrNull {
                it.isClickable
            }?.run {
                Timber.d("onAccessibilityEvent start paste")
                val arguments = Bundle()
                arguments.putCharSequence(
                        AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                        parkingnum)
                Timber.d("onAccessibilityEvent refresh")
                performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                Timber.d("onAccessibilityEvent pasteComplete")
                Timber.d("onAccessibilityEvent edittext = ${this.text}")
                sleep(250)

                success = true
            }
            editTextNodes.forEach { it.recycle() }
        }
        return success
    }

    private fun initView(widget: String, nodeInfo: AccessibilityNodeInfo?, depth: Int, nodes: HashMap<String, AccessibilityNodeInfo>) {
        if (nodeInfo == null) return
        for (i in 0 until nodeInfo.childCount) {
            if (nodeInfo.getChild(i).className == "android.widget.$widget")
                nodes["$widget$i"] = nodeInfo.getChild(i)
            initView(widget, nodeInfo.getChild(i), depth + 1, nodes);
        }
    }

    private fun handleParkClick(nodeInfo: AccessibilityNodeInfo, exitId: String): Boolean {
        var success = false
        if (!interrupted) {
            val buttonNodes = nodeInfo.findAccessibilityNodeInfosByViewId(PAY_ID)
            buttonNodes.firstOrNull {
                it.isClickable
            }?.run {
                performAction(AccessibilityNodeInfo.ACTION_CLICK)
                success = true
            }
            buttonNodes.forEach { it.recycle() }
        }
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
        finishPayId = id
    }
}