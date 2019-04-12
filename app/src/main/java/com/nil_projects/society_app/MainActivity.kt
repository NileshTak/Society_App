package com.nil_projects.society_app

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore

import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

import android.view.View

import com.google.android.material.navigation.NavigationView

import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import android.widget.*
import com.google.android.material.snackbar.BaseTransientBottomBar


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        supportFragmentManager.beginTransaction().replace(R.id.frame_container, HomeFrag()).commit()
        supportActionBar!!.title = "Home"

        val fab_add_Worker = findViewById<com.getbase.floatingactionbutton.FloatingActionButton>(R.id.add_worker_fab)
        fab_add_Worker.setOnClickListener {
            var intent = Intent(this, Add_Worker::class.java)
            startActivity(intent)
            Snackbar.make(fab_add_Worker, "Add Workers", BaseTransientBottomBar.LENGTH_SHORT)
                    .setAction("Action", null).show()
        }

        val fab_add_payment_update = findViewById<com.getbase.floatingactionbutton.FloatingActionButton>(R.id.update_payment_details_fab)
        fab_add_payment_update.setOnClickListener {
            var intent = Intent(this, Add_Payment_Update::class.java)
            startActivity(intent)
        }

        val fab_notification_update = findViewById<com.getbase.floatingactionbutton.FloatingActionButton>(R.id.update_notification_fab)
        fab_notification_update.setOnClickListener {
            var int = Intent(this , UpdateNotification :: class.java)
            startActivity(int)
        }


        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val holder = findViewById<LinearLayout>(R.id.holder)
        val toggle = object : ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

                //this code is the real player behind this beautiful ui
                // basically, it's a mathemetical calculation which handles the shrinking of
                // our content view

                val scaleFactor = 7f
                val slideX = drawerView.width * slideOffset

                holder.setTranslationX(slideX)
                holder.setScaleX(1 - slideOffset / scaleFactor)
                holder.setScaleY(1 - slideOffset / scaleFactor)

                super.onDrawerSlide(drawerView, slideOffset)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = window
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)// will remove all possible our aactivity's window bounds
        }

        drawer.addDrawerListener(toggle)

        drawer.setScrimColor(Color.TRANSPARENT)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
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
        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        when (id) {
            R.id.nav_home -> {
                loadHomeFrag(fragHome = HomeFrag())
            }
            R.id.nav_reports -> {
                loadReportFrag(fragReport = ReportFrag())
            }
            R.id.nav_userreq -> {
                loadUserReqFrag(fragUserReq = UserReqFrag())
            }
            R.id.nav_complaints -> {

            }
            R.id.nav_notification -> {
                loadNotifiFrag(fragNotifi = NotificationFrag())
            }
            R.id.nav_parking -> {

            }
            R.id.nav_workers -> {
                loadWorkersFrag(fragWorkers = WorkersFrag())
            }
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    fun loadHomeFrag(fragHome : HomeFrag)
    {
        val fm = supportFragmentManager.beginTransaction()
        supportActionBar!!.title = "Home"
        fm.replace(R.id.frame_container,fragHome)
        fm.commit()
    }

    fun loadUserReqFrag(fragUserReq : UserReqFrag)
    {
        val fm = supportFragmentManager.beginTransaction()
        supportActionBar!!.title = "User Requests"
        fm.replace(R.id.frame_container,fragUserReq)
        fm.commit()
    }

    fun loadReportFrag(fragReport : ReportFrag)
    {
        val fm = supportFragmentManager.beginTransaction()
        supportActionBar!!.title = "Reports"
        fm.replace(R.id.frame_container,fragReport)
        fm.commit()
    }

    fun loadWorkersFrag(fragWorkers : WorkersFrag)
    {
        val fm = supportFragmentManager.beginTransaction()
        supportActionBar!!.title = "Workers"
        fm.replace(R.id.frame_container,fragWorkers)
        fm.commit()
    }

    fun loadNotifiFrag(fragNotifi : NotificationFrag)
    {
        val fm = supportFragmentManager.beginTransaction()
        supportActionBar!!.title = "Notifications"
        fm.replace(R.id.frame_container,fragNotifi)
        fm.commit()
    }
}

class NotificationClass(val id : String,val notification: String)
{
    constructor() : this("","")
}