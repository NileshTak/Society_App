package com.nil_projects.society_app

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text


class FUllScreenImage : AppCompatActivity() {

    lateinit var fullscreenimg : PhotoView
    lateinit var ivBackArrow : Button
    lateinit var ivDownload : Button
    lateinit var tvMsg : TextView
    lateinit var collectionName : String
    lateinit var imgUri : Uri
    lateinit var id : String
    lateinit var ivDltImage : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)

        ivBackArrow = findViewById<Button>(R.id.ivBackArrow)
        tvMsg = findViewById<TextView>(R.id.tvFullPic)
        ivDltImage = findViewById<Button>(R.id.ivDltImage)
        ivDownload = findViewById<Button>(R.id.ivDownload)
        fullscreenimg = findViewById<View>(R.id.fullscreen_image) as PhotoView

        val window = this.getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(ContextCompat.getColor(FUllScreenImage@this, android.R.color.black))

        ivBackArrow.setOnClickListener {
            onBackPressed()
        }



        val bundle: Bundle? = intent.extras

        bundle?.let {
            val msg = bundle.getString("msg")
              id = bundle.getString("id")
            collectionName = bundle.getString("collectionName")
            if(msg.isNotEmpty() && id.isNotEmpty())
            {
                tvMsg.text = msg

            }
        }

        var img = intent
        if(img != null)
        {
            imgUri = img.data
            if(imgUri != null && fullscreenimg != null)
            {
                Glide.with(this).load(imgUri).into(fullscreenimg)
            }
        }

        ivDltImage.setOnClickListener {

            var db = FirebaseFirestore.getInstance()
            db.collection(collectionName).document( id )
                    .delete()
                    .addOnSuccessListener { Toast.makeText(this,"Successfully Deleted",Toast.LENGTH_LONG).show()
                    onBackPressed()
                    }
                    .addOnFailureListener { e -> Toast.makeText(this,"Network Error",Toast.LENGTH_LONG).show() }


        }

        ivDownload.setOnClickListener {
            var dm = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            if(imgUri != null)
            {
                var request = DownloadManager.Request(imgUri)
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                //        request.setDestinationInExternalFilesDir(this,DestinationDirectryLocation,filename+fileextension)
                dm.enqueue(request)
            }
        }
    }
}
