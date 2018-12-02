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


// is clickable android.widget.Button text= ОПЛАТИТЬ ru.mos.parking.mobile:id/parking_park_pay
//is clickable   android.widget.EditText text= Номер парковки / билета ru.mos.parking.mobile:id/parking_parkId
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

        private var BUTTON_BAR_ID = "ru.mos.parking.mobile:id/buttonPanel"
        private var NUMBER_PICKERS_ID = "ru.mos.parking.mobile:id/custom"

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

        if (finishPayId != exitId) {
            if (pasteParking(nodeInfo, parkingNum))
                if (handleParkClick(nodeInfo, exitId)) {
                    Timber.d("onAccessibilityEvent pay clicked, dialog opened")
                    setSuccess(exitId)
                }
        }

        val myNodes = HashMap<String, AccessibilityNodeInfo>()
       val dialog =  getDialog(rootInActiveWindow, myNodes)
        val buttonBar = getButtons(rootInActiveWindow, myNodes)
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
        Timber.d("onAccessibilityEvent button: ${node.text}")

        if (node.isClickable) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            action()
        }
    }

    private fun handleNumberPicker(node: AccessibilityNodeInfo, target: String, action: () -> Unit) {
        val hoursPickerText = node.getChild(1)?.text.toString()
        Timber.d("onAccessibilityEvent hours: $hoursPickerText ? $target")
        if (hoursPickerText != target) {
            Timber.d("onAccessibilityEvent start scroll")
            node.performAction(ACTION_SCROLL_FORWARD)
            sleep(50)
        } else {
            Timber.d("onAccessibilityEvent hours are the same")
            action()
        }
    }

    private fun logViewHierarchy(nodeInfo: AccessibilityNodeInfo?, depth: Int) {
        if (nodeInfo == null) return
        var spacerString = ""
        for (i in 0 until depth) {
            spacerString += '-'.toString()
        }
        Timber.d("onAccessibilityEvent " + spacerString + nodeInfo.className + " " + nodeInfo.viewIdResourceName)

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


    private fun getButtons(nodeInfo: AccessibilityNodeInfo?, nodes: HashMap<String, AccessibilityNodeInfo>): List<AccessibilityNodeInfo>? {
        nodeInfo ?: return null
        if (!interrupted) {
            val dialgNodes = nodeInfo.findAccessibilityNodeInfosByViewId(BUTTON_BAR_ID)

            if (dialgNodes != null)
                if (dialgNodes.size != 0) {
                    Timber.d("onAccessibilityEvent buttonBafFound, root is  ${dialgNodes[0].className}")
                    initDialogButtonsBar(dialgNodes[0], 0, nodes)
                    Timber.d("onAccessibilityEvent buttonBar found, childs = ${dialgNodes[0].childCount}")
                    return dialgNodes
                }

        }
        return null
    }

    private fun getDialog(nodeInfo: AccessibilityNodeInfo?, nodes: HashMap<String, AccessibilityNodeInfo>): List<AccessibilityNodeInfo>? {
        nodeInfo ?: return null
        var success = false
        if (!interrupted) {
            val dialgNodes = nodeInfo.findAccessibilityNodeInfosByViewId(NUMBER_PICKERS_ID)
            if (dialgNodes != null)
                if (dialgNodes.size != 0) {
                    initDialogInputTime(dialgNodes[0], 0, nodes)
                    Timber.d("onAccessibilityEvent dialog found, childs = ${dialgNodes[0].childCount}")
                    return dialgNodes
                }
        }
        return null
    }

    private fun pasteParking(nodeInfo: AccessibilityNodeInfo, parkingnum: String): Boolean {
        var success = false
        if (!interrupted) {
            val editTextNodes = nodeInfo.findAccessibilityNodeInfosByViewId("ru.mos.parking.mobile:id/parking_parkId")
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

    private fun listenParkingClick(nodeInfo: AccessibilityNodeInfo): Boolean {
        var success = false
        if (!interrupted) {
            val buttonNodes = nodeInfo.findAccessibilityNodeInfosByViewId("ru.mos.parking.mobile:id/parking_park_pay")
            Timber.d("onAccessibilityEvent node found")
            nodeInfo.findAccessibilityNodeInfosByText("NumberPicker")
            buttonNodes.firstOrNull {
                it.isClickable
            }?.run {
                if (this.isSelected) {
                    Timber.d("onAccessibilityEvent button is selected")
                    success = true
                }
            }


            buttonNodes.forEach { it.recycle() }
        }
        return success
    }

    private fun initDialogButtonsBar(nodeInfo: AccessibilityNodeInfo?, depth: Int, nodes: HashMap<String, AccessibilityNodeInfo>) {
        if (nodeInfo == null) return
        for (i in 0 until nodeInfo.childCount) {
            if (nodeInfo.getChild(i).className == "android.widget.Button")
                nodes["Button$i"] = nodeInfo.getChild(i)
            initDialogButtonsBar(nodeInfo.getChild(i), depth + 1, nodes);
        }
    }

    private fun initDialogInputTime(nodeInfo: AccessibilityNodeInfo?, depth: Int, nodes: HashMap<String, AccessibilityNodeInfo>) {
        if (nodeInfo == null) return;
        var spacerString = ""
        for (i in 0 until depth) {
            spacerString += '-';
        }

        for (i in 0 until nodeInfo.childCount) {
            if (nodeInfo.getChild(i).className == "android.widget.NumberPicker")
                nodes["NumberPicker$i"] = nodeInfo.getChild(i)
            initDialogInputTime(nodeInfo.getChild(i), depth + 1, nodes);
        }
    }


    private fun handleParkClick(nodeInfo: AccessibilityNodeInfo, exitId: String): Boolean {
        var success = false
        if (!interrupted) {
            val buttonNodes = nodeInfo.findAccessibilityNodeInfosByViewId("ru.mos.parking.mobile:id/parking_park_pay")
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