package com.example.v4n0v.parking.mvp

import android.annotation.SuppressLint
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
}