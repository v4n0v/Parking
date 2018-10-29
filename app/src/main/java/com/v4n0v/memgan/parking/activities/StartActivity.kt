package com.v4n0v.memgan.parking.activities

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.v4n0v.memgan.parking.R
import com.v4n0v.memgan.parking.fragments.FragmentNoService
import com.v4n0v.memgan.parking.fragments.FragmentSetDuration
import com.v4n0v.memgan.parking.fragments.FragmentStartParking
import com.v4n0v.memgan.parking.mvp.BaseActivity
import com.v4n0v.memgan.parking.mvp.presenters.MainPresenter
import com.v4n0v.memgan.parking.mvp.views.MainView
import com.v4n0v.memgan.parking.utils.Helper
import com.v4n0v.memgan.parking.utils.Helper.EXTRA_PAY_BUTTON_CLICKED
import com.v4n0v.memgan.parking.utils.Items
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import timber.log.Timber

private const val PERMISSION_FOR_ALL_REQUEST_CODE = 1654

class StartActivity : MainView, BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    enum class State { NO_SERVICE, PARKING, DURATION, TIMER }

    @InjectPresenter
    lateinit var presenter: MainPresenter


    @ProvidePresenter
    internal fun providePresenter(): MainPresenter {
        return MainPresenter()
    }

    override fun onResume() {
        super.onResume()

        presenter.checkState()
    }

    override fun initialiaze() {
        log(" StartActivity initialiazzzze")
        fab.setOnClickListener { _ ->
            toast("Нахер нужна эта кнопка?")
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        ivIconStatus.setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }

        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun switchFragment(state: State) {
        when (state) {
            State.TIMER -> {
                Timber.d("TIMER")
            }
            State.DURATION -> {
                beginTransaction(FragmentSetDuration())
            }
            State.PARKING -> {
                beginTransaction(FragmentStartParking())
            }
            State.NO_SERVICE -> {
                beginTransaction(FragmentNoService())
            }
        }
    }


    override fun checkState() {
        if (Helper.checkAccessibilityService(this)) {
            tvServiceStatus.text = getString(R.string.service_Ok)
            ivIconStatus.setImageDrawable(Helper.getDrawable(this, Items.OK))
            val clicked = intent.getBooleanExtra(EXTRA_PAY_BUTTON_CLICKED, false)
            if (clicked)
                switchFragment(State.DURATION)
            else
                switchFragment(State.PARKING)
            //   btnStartService.text = getString(R.string.service_stop)
        } else {
            tvServiceStatus.text = getString(R.string.service_unavailable)
            ivIconStatus.setImageDrawable(Helper.getDrawable(this, Items.UNAVAILABLE))
            switchFragment(State.NO_SERVICE)
        }

        if (!Helper.checkPermissions(this)) {
            requestPermissions()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else
            super.onBackPressed()
    }

    private fun requestPermissions() {
        val requiredPermissionsFromManifest = Helper.getNotRequestedPermissions(this)
        for (s in requiredPermissionsFromManifest) {
            if (ContextCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
                showInformDialog("Запрос разрешения", "В следующем диалоге нажмите \"Разрешить\" Иначе приложение не сможет работать!", DialogInterface.OnClickListener { _, _ ->
                    ActivityCompat.requestPermissions(this@StartActivity,
                            requiredPermissionsFromManifest.toTypedArray(),
                            PERMISSION_FOR_ALL_REQUEST_CODE)
                })
                return
            }
        }
        log("request complete")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
