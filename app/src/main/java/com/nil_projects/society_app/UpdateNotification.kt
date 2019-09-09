@file:Suppress("DEPRECATION")

package com.nil_projects.society_app

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_update_notification.*
import java.io.File
import java.util.*
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.StrictMode
import androidx.appcompat.app.AlertDialog
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.tapadoo.alerter.Alerter
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_update_report.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList


class UpdateNotification : AppCompatActivity() {

    lateinit var updatenoti: Button
    lateinit var display_img: ImageView
    lateinit var noti_edittext: EditText
    lateinit var progressDialog: ProgressDialog
    lateinit var currentTime : String
    var LoggedIn_User_Email: String? = null
    var counter : Long = 0
    lateinit var listMobileNo : ArrayList<String>


    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_notification)
        supportActionBar!!.title = "Update New Society Notice"

        val actionbar = supportActionBar
        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar!!.setDisplayHomeAsUpEnabled(true)


        updatenoti = findViewById<Button>(R.id.btn_update_notifi)
        noti_edittext = findViewById<EditText>(R.id.text_notification)
        display_img = findViewById<ImageView>(R.id.circular_notification_pic)


        LoggedIn_User_Email =FirebaseAuth.getInstance().currentUser!!.getEmail()
        listMobileNo = ArrayList<String>()

        display_img.setOnClickListener {
            askGalleryPermission()
        }

        var df = SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        currentTime = df.format(Calendar.getInstance().getTime());

        fetchuserMobilefromFirebase()

        updatenoti.setOnClickListener {
            progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Wait a Sec....Updating Notification")
            progressDialog.setCancelable(false)
            progressDialog.show()
            UpdateNotifcationtoFirebase()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun askGalleryPermission() {
        askPermission(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE){
            selectPhoto()
        }.onDeclined { e ->
            if (e.hasDenied()) {
                //the list of denied permissions
                e.denied.forEach {
                }

                AlertDialog.Builder(this@UpdateNotification)
                        .setMessage("Please accept our permissions.. Otherwise you will not be able to use some of our Important Features.")
                        .setPositiveButton("yes") { dialog, which ->
                            e.askAgain()
                        } //ask again
                        .setNegativeButton("no") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()
            }

            if(e.hasForeverDenied()) {
                //the list of forever denied permissions, user has check 'never ask again'
                e.foreverDenied.forEach {
                }
                // you need to open setting manually if you really need it
                e.goToSettings();
            }
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

          //  saveImage(bitmap)

            val uri = data.data

            compressImage(getRealPathFromURI(uri))

            Log.d("FilePath",getRealPathFromURI(uri))

        }
    }

    fun getRealPathFromURI(contentUri: Uri): String {

        // can post image
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = managedQuery(contentUri,
                proj, // WHERE clause selection arguments (none)
                null, null, null)// Which columns to return
        // WHERE clause; which rows to return (all rows)
        // Order-by clause (ascending by name)
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()

        return cursor.getString(column_index)
    }

    fun compressImage(filePath: String): String {

        var scaledBitmap: Bitmap? = null

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var bmp = BitmapFactory.decodeFile(filePath, options)

        var actualHeight = options.outHeight
        var actualWidth = options.outWidth
        val maxHeight = 1150.0f
        val maxWidth = 950.0f
        var imgRatio = (actualWidth / actualHeight).toFloat()
        val maxRatio = maxWidth / maxHeight

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()

            }
        }

        options.inSampleSize = calculateInSampleSize(options,
                actualWidth, actualHeight)
        options.inJustDecodeBounds = false
        options.inDither = false
        options.inScaled = false
        options.inPurgeable = true
        options.inInputShareable = true
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        options.inTempStorage = ByteArray(16 * 1024)


        try {
            bmp = BitmapFactory.decodeFile(filePath, options)
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,
                    Bitmap.Config.ARGB_8888)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }

        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f

        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)

        val canvas = Canvas(scaledBitmap)
        canvas.matrix = scaleMatrix
        canvas.drawBitmap(bmp, middleX - bmp.width / 2,
                middleY - bmp.height / 2, Paint(
                Paint.FILTER_BITMAP_FLAG))

        bmp.recycle()

        val exif: ExifInterface
        try {
            exif = ExifInterface(filePath)

            val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0)
            Log.d("EXIF", "Exif: $orientation")
            val matrix = Matrix()
            when (orientation) {
                6 -> matrix.postRotate(90f)
                3 -> matrix.postRotate(180f)
                8 -> matrix.postRotate(270f)
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap!!.width, scaledBitmap.height, matrix,
                    true)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        var out: FileOutputStream? = null
        val filename = getFilename()
        try {
            out = FileOutputStream(filename)
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 95, out)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        Log.d("FileName", filename)

        return filename

    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }

        return inSampleSize
    }

    fun getFilename(): String {

        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File(root + "/SocietyApp/Society_App_Notifications")
        if (!myDir.exists()) {
            myDir.mkdirs()
        }
        val generator = Random()
        var n = 10000
        n = generator.nextInt(n)

        val OutletFname = "NotificationImg-$n.jpg"

        val file = File(myDir, OutletFname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            selectedPhotoUri = Uri.fromFile(file)
            Log.d("FileName", selectedPhotoUri.toString())

            out.flush()
            out.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return (file.absolutePath)
    }


    private fun UpdateNotifcationtoFirebase() {

        if ( selectedPhotoUri  == null)
        {
            Alerter.create(this@UpdateNotification)
                    .setTitle("Society Notice")
                    .setIcon(R.drawable.alert)
                    .setDuration(4000)
                    .setText("Failed to Update!! Please Select Notice Picture to Update!!")
                    .setBackgroundColorRes(R.color.colorAccent)
                    .show()
            progressDialog.dismiss()
            return
        }
        else if (noti_edittext.text.toString().isEmpty())
        {
            noti_edittext.error = "Please Enter Valid Notification Status"
            progressDialog.dismiss()
            return
        }

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
                    Alerter.create(this@UpdateNotification)
                            .setTitle("Society Notice")
                            .setIcon(R.drawable.alert)
                            .setDuration(4000)
                            .setText("Failed to Add!! Please Try after some time!!")
                            .setBackgroundColorRes(R.color.colorAccent)
                            .show()
                }
    }

    private fun addNotificationtoFirebase(imgId: String) {

         val notifiid = UUID.randomUUID().toString()


        val items = HashMap<String, Any>()

        items.put("currentTime", currentTime)
        items.put("id", notifiid)
        items.put("imageUrl", imgId)
        items.put("noti", noti_edittext.text.toString())

        var db = FirebaseFirestore.getInstance()

        db.collection("Notifications")

                .get()
                .addOnSuccessListener {


                    if (it.isEmpty) {
                        items.put("counter", counter.toString())


                        db.collection("Notifications").document(notifiid)
                                .set(items).addOnSuccessListener {

                                    showAlert()

                                    sendFCMtoUsers()
                                    progressDialog.dismiss()
                                    var int = Intent(this@UpdateNotification, MainActivity::class.java)
                                    startActivity(int)


                                }.addOnFailureListener {
                                    Alerter.create(this@UpdateNotification)
                                            .setTitle("Society Notice")
                                            .setIcon(R.drawable.alert)
                                            .setDuration(4000)
                                            .setText("Failed to Add!! Please Try after some time!!")
                                            .setBackgroundColorRes(R.color.colorAccent)
                                            .show()

                                }

                    } else {

                        counter = it.size().toLong()

                        items.put("counter", counter.toString())


                        db.collection("Notifications").document(notifiid)
                                .set(items).addOnSuccessListener {

                                    showAlert()

                                    sendFCMtoUsers()
                                    progressDialog.dismiss()
                                    var int = Intent(this@UpdateNotification, MainActivity::class.java)
                                    startActivity(int)


                                }.addOnFailureListener {
                                    Alerter.create(this@UpdateNotification)
                                            .setTitle("Society Notice")
                                            .setIcon(R.drawable.alert)
                                            .setDuration(4000)
                                            .setText("Failed to Add!! Please Try after some time!!")
                                            .setBackgroundColorRes(R.color.colorAccent)
                                            .show()

                                }


                    }


                }







//        val ref = FirebaseDatabase.getInstance().getReference("/Notifications/$notifiid")
//        val refCounter = FirebaseDatabase.getInstance().reference.child("Notifications")
//
//        refCounter.addValueEventListener(object : ValueEventListener
//        {
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//
//            override fun onDataChange(p0: DataSnapshot) {
//                if(p0.exists())
//                {
//                    counter = p0.childrenCount
//                    val status = AddNotifiClass(notifiid,noti_edittext.text.toString(),imgId,currentTime,counter.toString())
//                    ref.setValue(status)
//                            .addOnSuccessListener {
//                                progressDialog.dismiss()
//                                showAlert()
//                                sendFCMtoUsers()
//                                onBackPressed()
//                            }
//                            .addOnFailureListener {
//                                Alerter.create(this@UpdateNotification)
//                                        .setTitle("Society Notice")
//                                        .setIcon(R.drawable.alert)
//                                        .setDuration(4000)
//                                        .setText("Failed to Update!! Please Try after some time!!")
//                                        .setBackgroundColorRes(R.color.colorAccent)
//                                        .show()
//                            }
//                }
//                else
//                {
//                    counter = 0
//                    val status = AddNotifiClass(notifiid,noti_edittext.text.toString(),imgId,currentTime,counter.toString())
//                    ref.setValue(status)
//                            .addOnSuccessListener {
//                                progressDialog.dismiss()
//                                showAlert()
//                                onBackPressed()
//                                sendFCMtoUsers()
//                            }
//                            .addOnFailureListener {
//                                Alerter.create(this@UpdateNotification)
//                                        .setTitle("Society Notice")
//                                        .setIcon(R.drawable.alert)
//                                        .setDuration(4000)
//                                        .setText("Failed to Update!! Please Try after some time!!")
//                                        .setBackgroundColorRes(R.color.colorAccent)
//                                        .show()
//                            }
//                }
//            }
//
//        })
    }

    private fun showAlert() {
        Alerter.create(this@UpdateNotification)
                .setTitle("Society Notice")
                .setIcon(R.drawable.society)
                .setText("Society Notice Successfully Posted!! #KeepPosting :)")
                .setBackgroundColorRes(R.color.colorAccent)
                .setDuration(4000)
                .show()
    }

    private fun fetchuserMobilefromFirebase()
    {
        var db = FirebaseFirestore.getInstance()
        db.collection("FlatUsers")
                .whereEqualTo("userAuth","Accepted")
                .get()
                .addOnSuccessListener { documentSnapshot ->

                    val city = documentSnapshot.toObjects(UserClass::class.java)
                    for (document in city) {
                        if (document != null) {
                            listMobileNo.add(document.MobileNumber)
                        }
                    }
                }

                .addOnFailureListener { exception ->
                    Log.w("SocietyFirestore", "Error getting documents.", exception)
                }
    }


    private fun sendFCMtoUsers() {

        AsyncTask.execute {
            val SDK_INT = android.os.Build.VERSION.SDK_INT
            if (SDK_INT > 8) {
                val policy = StrictMode.ThreadPolicy.Builder()
                        .permitAll().build()
                StrictMode.setThreadPolicy(policy)
                var sendNotificationID: String

                //This is a Simple Logic to Send Notification different Device Programmatically....
                if (LoggedIn_User_Email.equals("admin@gmail.com") && listMobileNo.isNotEmpty()) {
                    //send_email = "client@gmail.com"

                    for (i in 0..listMobileNo.size-1)
                    {
                        sendNotificationID = listMobileNo.get(i)
                        Log.d("OneSignal App",sendNotificationID)

                        try {
                            val jsonResponse: String

                            val url = URL("https://onesignal.com/api/v1/notifications")
                            val con = url.openConnection() as HttpURLConnection
                            con.setUseCaches(false)
                            con.setDoOutput(true)
                            con.setDoInput(true)

                            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                            con.setRequestProperty("Authorization", "Basic NzY1N2E5MGEtM2JjZi00MWU3LTg5ZjYtNjg5Y2Y4Nzg2ZTk0")
                            con.setRequestMethod("POST")

                            val strJsonBody = ("{"
                                    + "\"app_id\": \"1a84ca5e-eedd-4f38-9475-8e8c0e78bdfd\","

                                    + "\"filters\": [{\"field\": \"tag\", \"key\": \"NotificationID\", \"relation\": \"=\", \"value\": \"" + sendNotificationID + "\"}],"

                                    + "\"data\": {\"foo\": \"bar\"},"
                                    + "\"contents\": {\"en\": \"Having New Society Notice\"}"
                                    + "}")


                            println("strJsonBody:\n$strJsonBody")

                            val sendBytes = strJsonBody.toByteArray(charset("UTF-8"))
                            con.setFixedLengthStreamingMode(sendBytes.size)

                            val outputStream = con.getOutputStream()
                            outputStream.write(sendBytes)

                            val httpResponse = con.getResponseCode()
                            println("httpResponse: $httpResponse")

                            if (httpResponse >= HttpURLConnection.HTTP_OK && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                                val scanner = Scanner(con.getInputStream(), "UTF-8")
                                jsonResponse = if (scanner.useDelimiter("\\A").hasNext()) scanner.next() else ""
                                scanner.close()
                            } else {
                                val scanner = Scanner(con.getErrorStream(), "UTF-8")
                                jsonResponse = if (scanner.useDelimiter("\\A").hasNext()) scanner.next() else ""
                                scanner.close()
                            }
                            println("jsonResponse:\n$jsonResponse")

                        } catch (t: Throwable) {
                            t.printStackTrace()
                        }

                    }

                } else {
                    sendNotificationID = "admin@gmail.com"
                }
            }
        }
    }
}

