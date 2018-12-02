package com.v4n0v.memgan.parking.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuItem
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
import com.v4n0v.memgan.parking.utils.Helper.TIMER
import com.v4n0v.memgan.parking.utils.Helper.timerFormat
import com.v4n0v.memgan.parking.utils.Items
import kotlinx.android.synthetic.main.activity_launch.*
import kotlinx.android.synthetic.main.content_launch.*
import kotlinx.android.synthetic.main.fragment_parking.*

private const val PERMISSION_FOR_ALL_REQUEST_CODE = 1654
  const val PREFS_TIME = "prefs"
private const val SETTINGS_TIME = "time"
class LaunchActivity : MainView, BaseActivity() {


    enum class State { NO_SERVICE, PARKING, DURATION, TIMER }

    @InjectPresenter
    lateinit var presenter: MainPresenter


    @ProvidePresenter
    internal fun providePresenter(): MainPresenter {
        return MainPresenter()
    }

   private  var parkIntent: Intent? = null
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
