package com.example.v4n0v.parking.activities

import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.v4n0v.parking.R
import com.example.v4n0v.parking.mvp.BaseActivity
import com.example.v4n0v.parking.mvp.presenters.MainPresenter
import com.example.v4n0v.parking.mvp.views.MainView
import com.example.v4n0v.parking.utils.Helper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import timber.log.Timber


const val UNIQUE_PERIODIC_WORK_NAME = "sync"
private const val PERMISSION_FOR_ALL_REQUEST_CODE = 1654

class StartActivity : MainView, BaseActivity(), NavigationView.OnNavigationItemSelectedListener {


    @InjectPresenter
    lateinit var presenter: MainPresenter


    @ProvidePresenter
    internal fun providePresenter(): MainPresenter {
        return MainPresenter()
    }
    override fun initialiaze() {
        Timber.d(  " StartActivity initialiaze")
        fab.setOnClickListener { _ ->
            toast("FAAAAAAAB")
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        requestPermissions()
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
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
        Timber.d(  "request complete")
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
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
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
