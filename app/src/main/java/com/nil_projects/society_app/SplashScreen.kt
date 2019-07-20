package com.nil_projects.society_app

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.core.content.ContextCompat
import android.view.animation.AnimationUtils.loadAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView


class SplashScreen : AppCompatActivity() {

    lateinit var img : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        img = findViewById(R.id.app_image_view)

        val window = this.getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(ContextCompat.getColor(SplashScreen@this, R.color.md_blue_custom))

        val aniRotate = AnimationUtils.loadAnimation(applicationContext, R.anim.spin)
        img.startAnimation(aniRotate)

        Handler().postDelayed({
            var options =
                    ActivityOptions.makeCustomAnimation(this, R.anim.abc_fade_in, R.anim.abc_fade_out)
            startActivity(Intent(this,MainActivity :: class.java),options.toBundle())
        }, 4000)

    }

}
