package com.v4n0v.memgan.parking.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.v4n0v.memgan.parking.R
import com.v4n0v.memgan.parking.fragments.FragmentNoService
import com.v4n0v.memgan.parking.fragments.FragmentSetDuration
import com.v4n0v.memgan.parking.fragments.FragmentStartParking
import com.v4n0v.memgan.parking.fragments.FragmentTimer
import com.v4n0v.memgan.parking.mvp.BaseActivity
import com.v4n0v.memgan.parking.mvp.presenters.MainPresenter
import com.v4n0v.memgan.parking.mvp.views.MainView
import com.v4n0v.memgan.parking.utils.Helper
import com.v4n0v.memgan.parking.utils.Helper.MINUTE
import com.v4n0v.memgan.parking.utils.Helper.SECOND
import com.v4n0v.memgan.parking.utils.Items
import kotlinx.android.synthetic.main.activity_launch.*
import kotlinx.android.synthetic.main.content_launch.*
import javax.xml.datatype.DatatypeConstants.MINUTES
import android.R.id.edit
import android.content.SharedPreferences.Editor
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT


private const val PERMISSION_FOR_ALL_REQUEST_CODE = 1654
const val PREFS_TIME = "prefs"
private const val SETTINGS_TIME = "time"

class LaunchActivity : MainView, BaseActivity() {


    enum class State { NO_SERVICE, PARKING, DURATION, TIMER }


    var currentState = State.NO_SERVICE
    @InjectPresenter
    lateinit var presenter: MainPresenter


    @ProvidePresenter
    internal fun providePresenter(): MainPresenter {
        return MainPresenter()
    }

    private var parkIntent: Intent? = null
    private var clickIntent: Intent? = null

    override fun onResume() {
        super.onResume()

        presenter.checkState()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        setSupportActionBar(myToolbar)
    }

    @SuppressLint("SetTextI18n")
    override fun initialiaze() {
        ivIconStatus.setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                true
            }
            R.id.action_time -> {
                if (currentState == State.PARKING)
                    showTimeDialog()
                else
                    toastLong("Включите сервис и перейдите на экран настройки параметров парковки")
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
            State.DURATION -> beginTransaction(FragmentSetDuration())
            State.PARKING -> beginTransaction(FragmentStartParking())
            State.NO_SERVICE -> beginTransaction(FragmentNoService())
        }
        currentState = state
    }

    override fun checkState() {
        if (Helper.checkAccessibilityService(this)) {
            tvServiceStatus.text = getString(R.string.service_Ok)
            ivIconStatus.setImageDrawable(Helper.getDrawable(this, Items.OK))
            val clicked = intent.getBooleanExtra(Helper.EXTRA_PAY_BUTTON_CLICKED, false)
            if (clicked)
                switchFragment(State.DURATION)
            else
                switchFragment(State.PARKING)
        } else {
            tvServiceStatus.text = getString(R.string.service_unavailable)
            ivIconStatus.setImageDrawable(Helper.getDrawable(this, Items.UNAVAILABLE))
            switchFragment(State.NO_SERVICE)
        }

        if (!Helper.checkPermissions(this)) {
            requestPermissions()
        }
    }

    private fun showTimeDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle(resources.getString(R.string.action_change_time))
        val ll = LinearLayout(this)
        ll.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        ll.orientation = LinearLayout.VERTICAL
        val inputMins = EditText(this)
        val inputSecs = EditText(this)

        val etParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        etParams.setMargins(20, 10, 20, 10)
        inputMins.inputType = InputType.TYPE_CLASS_NUMBER
        inputMins.layoutParams = etParams
        inputSecs.layoutParams = etParams
        inputSecs.inputType = InputType.TYPE_CLASS_NUMBER
        inputMins.hint = "Минут"
        inputSecs.hint = "Cекунд"
        ll.addView(inputMins)
        ll.addView(inputSecs)
        builder.setView(ll)
        builder.setPositiveButton(resources.getString(R.string.button_done)
        ) { _, _ ->
            val mins = inputMins.text.toString().toLong()
            val secs = inputSecs.text.toString().toLong()
            val total = mins * MINUTE + secs * SECOND
            val sp = getSharedPreferences(PREFS_TIME, MvpAppCompatActivity.MODE_PRIVATE)
            val e = sp.edit()
            e.putLong("time", total)
            e.apply()
            switchFragment(State.PARKING)
        }
        builder.show()
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
