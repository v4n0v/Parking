package com.example.v4n0v.parking.mvp

import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatActivity
import timber.log.Timber

open class BaseActivity:MvpAppCompatActivity(){

    fun log(msg:String){
        Timber.d(msg)
    }

    fun toast(msg:String){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
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
}