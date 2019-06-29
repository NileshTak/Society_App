package com.nil_projects.society_app

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.media.Image
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.activity_update_report.*
import kotlinx.android.synthetic.main.custom_records_layout.view.*
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class UpdateReport : AppCompatActivity() {

    lateinit var img_select_camera: ImageView
    lateinit var imgGifCamera : ImageView
    lateinit var spinner_wing : Spinner
    val REQUEST_PERM_WRITE_STORAGE = 102
    lateinit var datePickerdialog : DatePickerDialog
    private val CAPTURE_PHOTO = 104
    internal var imagePath: String? = ""
    var formate = SimpleDateFormat("dd MMM, yyyy", Locale.US)
    lateinit var progressDialog: ProgressDialog
    var counter : Long = 0
    var spin_value : String = "Wing"
    var LoggedIn_User_Email: String? = null
    var imageUri  : Uri? = null
    lateinit var listMobileNo : ArrayList<String>
    lateinit var listWingName : ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_report)
        supportActionBar!!.title = "Update New Building Notice"

        img_select_camera = findViewById<ImageView>(R.id.img_select_camera)
        var btn_update = findViewById<Button>(R.id.btn_update)
        spinner_wing = findViewById<Spinner>(R.id.spinner_wing)
        var date_editText = findViewById<EditText>(R.id.date_editText)
        imgGifCamera = findViewById<ImageView>(R.id.imgGifCamera)
        LoggedIn_User_Email = FirebaseAuth.getInstance().currentUser!!.getEmail()
        listMobileNo = ArrayList<String>()
        listWingName = ArrayList<String>()

        Glide.with(this@UpdateReport).asGif().load(R.drawable.fab).into(imgGifCamera)

        val bundle: Bundle? = intent.extras
        if(bundle != null)
        {
            imgGifCamera.visibility = View.GONE
            var uri = bundle!!.getString("ImageUri")
            imageUri = uri.toUri()
            Log.d("CameraUri",imageUri.toString())
            Glide.with(UpdateReport@this).load(imageUri).into(img_select_camera)
        }

        date_editText.setOnClickListener {
            datePicker()
        }

        fetchuserMobilefromFirebase()

        val optionsWings = arrayOf("Madhumalti Building", "Row House","Aboli Building","Nishigandha Building","Sayali Building","Sonchafa Building")

        spinner_wing.adapter = ArrayAdapter<String>(applicationContext,R.layout.spinner_textview,optionsWings)
        spinner_wing.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(applicationContext,"Please Select Society Wing", Toast.LENGTH_LONG).show()
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Toast.makeText(applicationContext,"Selected Society Wing is : "+optionsWings.get(position), Toast.LENGTH_LONG).show()

                spin_value = optionsWings.get(position)
            }
        }


        img_select_camera.setOnClickListener {
            askCameraPermission()
        }

        btn_update.setOnClickListener {
            progressDialog = ProgressDialog(UpdateReport@this)
            progressDialog.setMessage("Wait a Sec....Posting Your Record")
            progressDialog.setCancelable(false)
            progressDialog.show()
            UploadImgtoFirebase()
        }
    }

    private fun askCameraPermission() {
        askPermission(Manifest.permission.CAMERA){
            var int = Intent(UpdateReport@this,Camera2APIScreen::class.java)
            startActivity(int)
        }.onDeclined { e ->
            if (e.hasDenied()) {
                //the list of denied permissions
                e.denied.forEach {
                }

                AlertDialog.Builder(this@UpdateReport)
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

    private fun datePicker() {
        val now = Calendar.getInstance()
        datePickerdialog = DatePickerDialog(UpdateReport@this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(Calendar.YEAR,year)
            selectedDate.set(Calendar.MONTH,month)
            selectedDate.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            val date = formate.format(selectedDate.time)
            Toast.makeText(applicationContext,"date : " + date,Toast.LENGTH_SHORT).show()
            date_editText.setText(date)
        },
                now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))
        datePickerdialog.show()
    }

    private fun UploadImgtoFirebase() {
        Log.d("SocietyLogs","Uri is Uplod"+imageUri.toString())
        if(imageUri == null || date_editText.text.isEmpty())
        {
            progressDialog.dismiss()
            Toast.makeText(applicationContext,"Please Select Valid Image & Valid Data",Toast.LENGTH_LONG).show()
            return
        }


        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/RecordImages/$filename")

        ref.putFile(imageUri!!)
                .addOnSuccessListener {
                    Toast.makeText(applicationContext,"Image Uploaded",Toast.LENGTH_LONG).show()
                    Log.d("SocietyLogs","Image uploaded")
                    ref.downloadUrl.addOnSuccessListener {
                        it.toString()
                        saveRecordDatetoFirebase(it.toString())
                    }
                }
                .addOnFailureListener {

                }
    }

    private fun saveRecordDatetoFirebase(ImageUrl : String)
    {
        val recordid = UUID.randomUUID().toString()
        val ref = FirebaseDatabase.getInstance().getReference("/RecordsDates/$recordid")

        val refrec = FirebaseDatabase.getInstance().getReference("/RecordsDates")

        refrec.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    counter = p0.childrenCount
                }
                else
                {
                    counter = 0
                }
            }

        })

        val status = RecordClass(recordid,date_editText.text.toString(),ImageUrl,counter.toString(),spin_value)
        ref.setValue(status)
                .addOnSuccessListener {
                    showAlert()
                    sendFCMtoUsers()
                    progressDialog.dismiss()
                    var int = Intent(this,MainActivity::class.java)
                    startActivity(int)
                }
                .addOnFailureListener {
                    Alerter.create(this@UpdateReport)
                            .setTitle("Building Notice")
                            .setIcon(R.drawable.alert)
                            .setDuration(4000)
                            .setText("Failed to Update!! Please Try after some time!!")
                            .setBackgroundColorRes(R.color.colorAccent)
                            .show()
                }
    }

    private fun showAlert() {
        Alerter.create(this@UpdateReport)
                .setTitle("Building Notice")
                .setIcon(R.drawable.build)
                .setText("Building Notice Successfully Posted!! #KeepPosting :)")
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
                            listWingName.add(document.Wing)
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
                        if(listWingName.get(i) == spin_value)
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
                                con.setRequestProperty("Authorization", "Basic Y2Q3ODRhYTUtMjA4ZC00NTZjLTg3MDktMzEwNjJkOWMwMTRi")
                                con.setRequestMethod("POST")

                                val strJsonBody = ("{"
                                        + "\"app_id\": \"69734071-08a8-4d63-a7ab-adda8e2197f0\","

                                        + "\"filters\": [{\"field\": \"tag\", \"key\": \"NotificationID\", \"relation\": \"=\", \"value\": \"" + sendNotificationID + "\"}],"

                                        + "\"data\": {\"foo\": \"bar\"},"
                                        + "\"contents\": {\"en\": \"Having New Building Notice\"}"
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

                    }

                } else {
                    sendNotificationID = "admin@gmail.com"
                }
            }
        }
    }
}
