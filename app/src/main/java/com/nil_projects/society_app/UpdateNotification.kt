package com.nil_projects.society_app

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import com.github.chrisbanes.photoview.PhotoView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add__worker.*
import kotlinx.android.synthetic.main.activity_update_notification.*
import java.util.*

class UpdateNotification : AppCompatActivity() {

    lateinit var updatenoti: Button
    lateinit var display_img: ImageView
    lateinit var noti_edittext: EditText
    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_notification)

        updatenoti = findViewById<Button>(R.id.btn_update_notifi)
        noti_edittext = findViewById<EditText>(R.id.text_notification)
        display_img = findViewById<ImageView>(R.id.circular_notification_pic)

        pic_notification.setOnClickListener {
            selectPhoto()
        }

        updatenoti.setOnClickListener {
            progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Wait a Sec....Updating Notification")
            progressDialog.setCancelable(false)
            progressDialog.show()
            UpdateNotifcationtoFirebase()
        }
    }


    private fun selectPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            circular_notification_pic.setImageBitmap(bitmap)

            pic_notification.alpha = 0f
        }
    }

    private fun UpdateNotifcationtoFirebase() {

        val Notificationfilename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/Notifications/$Notificationfilename")

        ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Toast.makeText(applicationContext,"Notification Image Uploaded", Toast.LENGTH_LONG).show()
                    Log.d("SocietyLogs","Image uploaded")
                    ref.downloadUrl.addOnSuccessListener {
                        it.toString()
                        addNotificationtoFirebase(it.toString())
                    }
                }
                .addOnFailureListener {

                }
    }

    private fun addNotificationtoFirebase(imgId: String) {

        val notifiid = UUID.randomUUID().toString()
        val ref = FirebaseDatabase.getInstance().getReference("/Notifications/$notifiid")

        val status = AddNotifiClass(notifiid,noti_edittext.text.toString(),imgId)
        ref.setValue(status)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext,"Notification Added Successfully",Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext,"Failed to Update",Toast.LENGTH_LONG).show()
                }
    }

}

class AddNotifiClass(val id : String,val noti : String,val imageUrl : String)
{
    constructor() : this("","","")
}