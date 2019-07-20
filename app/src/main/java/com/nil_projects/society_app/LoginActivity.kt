package com.nil_projects.society_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var email : EditText
    lateinit var emailtxt : String
    lateinit var passtxt : String
    lateinit var pass : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        email = findViewById<EditText>(R.id.login_email)
        pass = findViewById<EditText>(R.id.login_pass)

        val window = this.getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(ContextCompat.getColor(FUllScreenImage@this, R.color.md_blue_custom))

        btn_login.setOnClickListener {
            Login()
        }
    }

    private fun Login() {
        emailtxt = email.text.toString()
        passtxt = pass.text.toString()

        check(emailtxt,passtxt)
    }

    private fun check(value: String, lazyMessage: String) {
        if(value.isEmpty())
        {
            email.error = "Enter Valid Email Address"
        }
        else if(lazyMessage.isEmpty())
        {
            pass.error = "Enter Valid Password"
        }
        else{

            FirebaseAuth.getInstance().signInWithEmailAndPassword(emailtxt,passtxt)
                    .addOnSuccessListener {
                        var int = Intent(this,MainActivity :: class.java)
                        Alerter.create(this@LoginActivity)
                                .setTitle("Admin")
                                .setIcon(R.drawable.noti)
                                .setDuration(4000)
                                .setText("Successfully Logged In!! :)")
                                .setBackgroundColorRes(R.color.colorAccent)
                                .show()
                        startActivity(int)
                        finish()
                    }.addOnFailureListener {
                        Alerter.create(this@LoginActivity)
                                .setTitle("Admin")
                                .setIcon(R.drawable.alert)
                                .setDuration(4000)
                                .setText("Log In Failed!! Incorrect UserName or Password")
                                .setBackgroundColorRes(R.color.colorAccent)
                                .show()
                    }
        }
    }

}
