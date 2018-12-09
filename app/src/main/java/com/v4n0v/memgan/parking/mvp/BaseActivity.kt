package com.v4n0v.memgan.parking.mvp

import android.content.DialogInterface
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.v4n0v.memgan.parking.R
import timber.log.Timber

open class BaseActivity: AppCompatActivity(){

    fun log(msg:String){
        Timber.d(msg)
    }

    fun toast(msg:String){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
    fun toastLong(msg:String){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    fun showInformDialog(title:String, message: String, onClickListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("ОК", onClickListener)
                .create()
                .show()
    }

    fun beginTransaction(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right)
//                .addToBackStack(null)
                .commit()
    }
}