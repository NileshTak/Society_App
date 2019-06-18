package com.nil_projects.society_app

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log

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
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.firebase.auth.FirebaseAuth
import com.onesignal.OneSignal
import kotlinx.android.synthetic.main.app_bar_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var mAuth : FirebaseAuth
    lateinit var btn_logout : Button
    var LoggedIn_User_Email: String? = null
    lateinit var tvNavTitle : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvNavTitle = findViewById<TextView>(R.id.tvnavTitle)
        btn_logout = findViewById<Button>(R.id.btn_logout)

        mAuth = FirebaseAuth.getInstance()
        var user = mAuth.currentUser

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init()


        if (user != null) {
            LoggedIn_User_Email =user!!.getEmail()
        }else{
            startActivity(Intent(this, LoginActivity::class.java))
        }

        OneSignal.sendTag("NotificationID", LoggedIn_User_Email);


        btn_logout.setOnClickListener {
            mAuth.signOut()
            Toast.makeText(this,"LogOut Successfully",Toast.LENGTH_LONG).show()
            startActivity(Intent(this, LoginActivity::class.java))
        }

        askImpPermissions()
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, HomeFrag()).commit()
        supportActionBar!!.title = ""
        tvNavTitle.text = "Home"

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

        val fab_report_update = findViewById<com.getbase.floatingactionbutton.FloatingActionButton>(R.id.update_report_fab)
        fab_report_update.setOnClickListener {
            var int = Intent(this , UpdateReport :: class.java)
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

    private fun askImpPermissions() {
        askPermission(Manifest.permission.INTERNET,Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
                ,Manifest.permission.ACCESS_NETWORK_STATE){

        }.onDeclined { e ->
            if (e.hasDenied()) {
                //the list of denied permissions
                e.denied.forEach {
                }

                AlertDialog.Builder(this@MainActivity)
                        .setMessage("Please accept our permissions.. Otherwise you will not be able to use some of our Important Features.")
                        .setPositiveButton("yes") { dialog, which ->
                            e.askAgain()
                        } //ask again
                        .setNegativeButton("no") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()
            }

            if (e.hasForeverDenied()) {
                //the list of forever denied permissions, user has check 'never ask again'
                e.foreverDenied.forEach {
                }
                // you need to open setting manually if you really need it
                e.goToSettings();
            }
        }
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
        when(item?.itemId)
        {
            R.id.view_users ->
            {
                var int = Intent(this,User_profiles_list :: class.java)
                startActivity(int)
            }
        }
        return super.onOptionsItemSelected(item)
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
                loadComplaintFrag(fragmentComplaint = ComplaintsFragment())
            }
            R.id.nav_notification -> {
                loadNotifiFrag(fragNotifi = NotificationFrag())
            }
            R.id.nav_maintainance -> {
                loadMaintainanceFrag(fragMaintainance = Maintainance_Records())
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
        supportActionBar!!.title = ""
        tvNavTitle.text = "Home"
        fm.replace(R.id.frame_container,fragHome)
        fm.commit()
    }

    fun loadUserReqFrag(fragUserReq : UserReqFrag)
    {
        val fm = supportFragmentManager.beginTransaction()
        supportActionBar!!.title = ""
        tvNavTitle.text = "User Requests"
        fm.replace(R.id.frame_container,fragUserReq)
        fm.commit()
    }

    fun loadReportFrag(fragReport : ReportFrag)
    {
        val fm = supportFragmentManager.beginTransaction()
        supportActionBar!!.title = ""
        tvNavTitle.text = "Building Notice"
        fm.replace(R.id.frame_container,fragReport)
        fm.commit()
    }

    fun loadWorkersFrag(fragWorkers : WorkersFrag)
    {
        val fm = supportFragmentManager.beginTransaction()
        supportActionBar!!.title = ""
        tvNavTitle.text = "Workers"
        fm.replace(R.id.frame_container,fragWorkers)
        fm.commit()
    }

    fun loadNotifiFrag(fragNotifi : NotificationFrag)
    {
        val fm = supportFragmentManager.beginTransaction()
        supportActionBar!!.title = ""
        tvNavTitle.text = "Society Notice"
        fm.replace(R.id.frame_container,fragNotifi)
        fm.commit()
    }

    fun loadComplaintFrag(fragmentComplaint : ComplaintsFragment)
    {
        val fm = supportFragmentManager.beginTransaction()
        supportActionBar!!.title = ""
        tvNavTitle.text = "Complaint Box"
        fm.replace(R.id.frame_container,fragmentComplaint)
        fm.commit()
    }

    fun loadMaintainanceFrag(fragMaintainance : Maintainance_Records)
    {
        val fm = supportFragmentManager.beginTransaction()
        supportActionBar!!.title = ""
        tvNavTitle.text = "Maintainance Records"
        fm.replace(R.id.frame_container,fragMaintainance)
        fm.commit()
    }

}
