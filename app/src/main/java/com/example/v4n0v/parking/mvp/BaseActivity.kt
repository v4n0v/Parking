package com.example.v4n0v.parking.mvp

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatActivity

open class BaseActivity:MvpAppCompatActivity(){

    @SuppressLint("LogNotTimber")
    fun log(msg:String){
        Log.d(this.javaClass.simpleName, msg)
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