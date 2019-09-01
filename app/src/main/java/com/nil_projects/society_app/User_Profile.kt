package com.nil_projects.society_app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_user__profile.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class User_Profile : AppCompatActivity() {

    lateinit var useranme : TextView
    lateinit var relation : TextView
    lateinit var flatno : TextView
    lateinit var wingname : TextView
    lateinit var profPic : CircleImageView
    lateinit var number : String
    private val REQUEST_CALL = 1


    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user__profile)
        supportActionBar!!.title = "Users Profile"

        useranme = findViewById<TextView>(R.id.Username_user_profile)
        profPic = findViewById<CircleImageView>(R.id.prof_pic_user_profile)
        wingname = findViewById<TextView>(R.id.wingname_profile)
        flatno = findViewById<TextView>(R.id.flatno_profile)
        relation = findViewById<TextView>(R.id.sidelabel_role)

        var bundle : Bundle? = intent.extras
        val UserName = bundle!!.getString("UserNameExtra")
        val profImg = bundle!!.getString("ProfileExtra")
        val flatNo = bundle!!.getString("flatnoExtra")
        val wingName = bundle!!.getString("wingnameExtra")
        val role = bundle!!.getString("roleExtra")
        number = bundle!!.getString("numberExtra")

        //Picasso.get().load(profImg).into(profPic)
        Glide.with(this).load(profImg).into(profPic)
        useranme.text = UserName
        relation.text = role
        flatno.text = "Flat No. : " +flatNo
        wingname.text = wingName

        swipe_btn_call.setOnStateChangeListener { active ->
            if (active) {
                Toast.makeText(this, "Calling", Toast.LENGTH_SHORT).show()
                makeCall()
            } else {
                Toast.makeText(this, "Calling Cancelled", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun makeCall() {
        if (number.trim({ it <= ' ' }).length > 0) {

            if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.CALL_PHONE
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL
                )
            } else {
                val dial = "tel:$number"
                startActivity(Intent(Intent.ACTION_CALL, Uri.parse(dial)))
            }

        } else {
            Toast.makeText(this, "Enter Phone Number", Toast.LENGTH_SHORT).show()
        }
    }
}
