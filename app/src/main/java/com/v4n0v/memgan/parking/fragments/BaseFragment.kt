package com.v4n0v.memgan.parking.fragments

import android.content.Context
import android.content.DialogInterface
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.v4n0v.memgan.parking.R

import timber.log.Timber

open class BaseFragment:Fragment(){

    fun log(msg:String){
        Timber.d(msg)
    }

    fun toast(msg:String){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun getColor(id:Int):Int{
      return  ContextCompat.getColor(activity as Context, R.color.colorAccent)
    }
    fun showInformDialog(title:String, message: String, onClickListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(context!!)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("ОК", onClickListener)
                .create()
                .show()
    }

}
