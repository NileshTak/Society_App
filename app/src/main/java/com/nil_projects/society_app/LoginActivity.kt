package com.nil_projects.society_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
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

        val currentUser = mAuth!!.currentUser
        if (currentUser != null) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }

        btn_login.setOnClickListener {
            Login()
        }
    }

    private fun Login() {
        emailtxt = email.text.toString()
        passtxt = pass.text.toString()

        FirebaseAuth.getInstance().signInWithEmailAndPassword(emailtxt,passtxt)
                .addOnCompleteListener {
                    var int = Intent(this,MainActivity :: class.java)
                    startActivity(int)
                    finish()

                }.addOnFailureListener {
                    Toast.makeText(this,"Inscorrect Password or email",Toast.LENGTH_LONG).show()
                }
    }
}
