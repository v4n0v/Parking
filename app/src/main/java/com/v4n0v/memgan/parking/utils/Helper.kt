package com.v4n0v.memgan.parking.utils

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import com.v4n0v.memgan.parking.R
import timber.log.Timber

enum class Items { UNAVAILABLE, OK }

object Helper {
    const val EXTRA_IS_READY_TO_PARK = "extra is ready to park"
    const val EXIT_ID = "exit id"
    const val ACTION_PARKING_TIME = "action parking time"
    const val PACKAGE_NAME = "ru.mos.parking.mobile"

    fun checkPermissions(context: Context): Boolean {
        with(context) {
            val requiredPermissionsFromManifest = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS).requestedPermissions
            for (s in requiredPermissionsFromManifest) {
                if (ContextCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
            return true
        }
    }

    fun checkAccessibilityService(context: Context): Boolean {
        with(context) {
            val id = getString(R.string.accessibilityservice_id)

            val am = context
                    .getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

            val runningServices = am
                    .getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK)
            for (service in runningServices) {
                Timber.d("ServiceId = ${service.id}")
                if (id == service.id) {
                    return true
                }
            }
            return false
        }
    }

    fun getDrawable(c: Context, item: Items): Drawable? {
        return when (item) {
            Items.UNAVAILABLE -> ResourcesCompat.getDrawable(c.resources, R.drawable.ic_do_not_disturb, null)
            Items.OK -> ResourcesCompat.getDrawable(c.resources, R.drawable.ic_done_green, null)
        }

    }

    fun getNotRequestedPermissions(context: Context): List<String> {
        with(context) {
            val list = mutableListOf<String>()
            val requiredPermissionsFromManifest = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS).requestedPermissions
            if (requiredPermissionsFromManifest != null)
                for (s in requiredPermissionsFromManifest) {
                    if (ContextCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
                        list.add(s)
                    }
                }
            return list
        }
    }
}