package com.v4n0v.memgan.parking.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.*
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.v4n0v.memgan.parking.R
import com.v4n0v.memgan.parking.fragments.FragmentNoService
import com.v4n0v.memgan.parking.fragments.FragmentStartParking
import com.v4n0v.memgan.parking.fragments.FragmentTimer
import com.v4n0v.memgan.parking.mvp.BaseActivity
import com.v4n0v.memgan.parking.mvp.presenters.MainPresenter
import com.v4n0v.memgan.parking.mvp.views.MainView
import com.v4n0v.memgan.parking.utils.Helper
import kotlinx.android.synthetic.main.activity_launch.*
import kotlinx.android.synthetic.main.content_launch.*
import android.widget.*
import com.v4n0v.memgan.parking.fragments.FragmentTutorial
import com.v4n0v.memgan.parking.utils.Helper.TIMER
import com.v4n0v.memgan.parking.utils.Helper.timerFormat
import android.support.v4.content.ContextCompat.getSystemService
import android.view.inputmethod.InputMethodManager


private const val PERMISSION_FOR_ALL_REQUEST_CODE = 1654
const val PREFS_TIME = "prefs"


class LaunchActivity : MainView, BaseActivity() {
    override fun markTutorialViewed() {
        getSharedPreferences("tutorial", Context.MODE_PRIVATE)
                .edit()
                .putBoolean("isTutorViewed", true)
                .apply()
    }

    companion object {
        const val SETTINGS_TIME = "time"
        var lastTime = 0L
    }

    enum class State { NO_SERVICE, PARKING, TIMER, TUTORIAL }

    //todo верни
//    var currentState = State.NO_SERVICE
    var currentState = State.TUTORIAL

    private var parkIntent: Intent? = null
    private var clickIntent: Intent? = null

    override fun onResume() {
        super.onResume()
        initialiaze()
        checkState()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        setSupportActionBar(myToolbar)
    }

    @SuppressLint("SetTextI18n")
    override fun initialiaze() {
//        switchStatus.setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        switchStatus.setOnCheckedChangeListener { _, _ ->
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

    }

    override fun onBackPressed() {
        if(System.currentTimeMillis()- lastTime > 3000) {
            lastTime = System.currentTimeMillis()
            super.onBackPressed()
        } else {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(switchStatus.getWindowToken(), 0)
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
//            R.id.action_settings -> {
//                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
//                true
//            }
            R.id.action_time -> {
                if (currentState == State.PARKING)
                    showTimeDialog()
                else
                    toastLong("Включите сервис и перейдите на экран настройки параметров парковки")
                true
            }
            R.id.action_tutorial -> {
                hideSwitch()
                switchFragment(State.TUTORIAL)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun startParking() {
        startActivity(parkIntent)
        applicationContext?.sendStickyBroadcast(clickIntent)
    }

    override fun initIntent(clickIntent: Intent, parkIntent: Intent) {
        this.parkIntent = parkIntent
        this.clickIntent = clickIntent
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun switchFragment(state: State) {
        when (state) {
            State.TIMER -> beginTransaction(FragmentTimer())
            State.PARKING -> beginTransaction(FragmentStartParking())
            State.NO_SERVICE -> beginTransaction(FragmentNoService())
            State.TUTORIAL -> beginTransaction(FragmentTutorial())
        }
        currentState = state
    }

    override fun checkState() {
        val sp = getSharedPreferences("tutorial", Context.MODE_PRIVATE)
        val isViewed = sp.getBoolean("isTutorViewed", false)
        if (!isViewed) {
            hideSwitch()
            switchFragment(State.TUTORIAL)
        } else {
            if (Helper.checkAccessibilityService(this)) {
                tvServiceStatus.text = getString(R.string.service_Ok)
                switchStatus.isChecked = true
                switchFragment(State.PARKING)
            } else {
                tvServiceStatus.text = getString(R.string.service_unavailable)
                switchStatus.isChecked = false
                switchFragment(State.NO_SERVICE)
            }
        }

        if (!Helper.checkPermissions(this))
            requestPermissions()

    }

    override fun saveTimePreferences(time: Long) {
        val sp = getSharedPreferences(PREFS_TIME, MvpAppCompatActivity.MODE_PRIVATE)
        val e = sp.edit()
        e.putLong(SETTINGS_TIME, time)
        e.apply()
    }

    private fun showTimeDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle(resources.getString(R.string.action_change_time))
        val ll = LinearLayout(this)
        ll.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        ll.orientation = LinearLayout.VERTICAL


        val prefs = getSharedPreferences(PREFS_TIME, MvpAppCompatActivity.MODE_PRIVATE)
        val time = prefs?.getLong(SETTINGS_TIME, Helper.TIMER)
        var currentTime = 0L
        val timeTv = TextView(this)
        timeTv.textSize = 80f
        timeTv.gravity = Gravity.CENTER
        timeTv.text = timerFormat.format(time)
        val seek = SeekBar(this)

        seek.max = TIMER.toInt()
        seek.progress = time?.toInt() ?: Helper.TIMER.toInt()


        seek.incrementProgressBy(5000)
        seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                timeTv.text = timerFormat.format(progress)
                currentTime = progress.toLong()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        ll.addView(timeTv)
        ll.addView(seek)
        builder.setView(ll)
        builder.setPositiveButton(resources.getString(R.string.button_done)
        ) { _, _ ->
            saveTimePreferences(currentTime)
            switchFragment(State.PARKING)
        }
        builder.show()
    }

    override fun showSwitch() {
        switchContainer.visibility = View.VISIBLE
    }

    override fun hideSwitch() {
        switchContainer.visibility = View.GONE
    }


    private fun requestPermissions() {
        val requiredPermissionsFromManifest = Helper.getNotRequestedPermissions(this)
        for (s in requiredPermissionsFromManifest) {
            if (ContextCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
                showInformDialog("Запрос разрешения", "В следующем диалоге нажмите \"Разрешить\" Иначе приложение не сможет работать!", DialogInterface.OnClickListener { _, _ ->
                    ActivityCompat.requestPermissions(this@LaunchActivity,
                            requiredPermissionsFromManifest.toTypedArray(),
                            PERMISSION_FOR_ALL_REQUEST_CODE)
                })
                return
            }
        }
    }

}
