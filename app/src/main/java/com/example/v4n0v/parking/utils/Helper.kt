package com.example.v4n0v.parking.utils

import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat

object Helper {

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