package com.nil_projects.society_app

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.activity_add__worker.*
import kotlinx.android.synthetic.main.fragment_report.*
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class Add_Worker : AppCompatActivity() {

    lateinit var spinner_type_worker : Spinner
    var spin_value_worker : String = "Type"
    internal var imagePath: String? = ""
    lateinit var date_editText_worker : EditText
    lateinit var datePickerdialog_worker : DatePickerDialog
    val REQUEST_PERM_WRITE_STORAGE = 102
    private val CAPTURE_PHOTO = 104
    var formate = SimpleDateFormat("dd MMM, yyyy", Locale.US)
    lateinit var btn_add_Worker : Button
    lateinit var progressDialog: ProgressDialog
    lateinit var edSpeciality : EditText
    var LoggedIn_User_Email: String? = null
    lateinit var listMobileNo : ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add__worker)
        supportActionBar!!.title = "Add Worker"

        spinner_type_worker = findViewById<Spinner>(R.id.spinner_type_worker)
        edSpeciality = findViewById<EditText>(R.id.edSpeciality)
        btn_add_Worker = findViewById<Button>(R.id.btn_add_worker)
        date_editText_worker = findViewById<EditText>(R.id.date_worker_joining)

        LoggedIn_User_Email = FirebaseAuth.getInstance().currentUser!!.getEmail()
        listMobileNo = ArrayList<String>()


        fetchuserMobilefromFirebase()

        date_editText_worker.setOnClickListener {
            dateJoiningWorker()
        }

        val optionsWorkers = arrayOf("Cook","Driver","Security Guard","Car Cleaner")

        spinner_type_worker.adapter = ArrayAdapter<String>(this,R.layout.spinner_textview,optionsWorkers)
        spinner_type_worker.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(applicationContext,"Please Select Worker Type", Toast.LENGTH_LONG).show()
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Toast.makeText(applicationContext,"Selected Worker Type is : "+optionsWorkers.get(position), Toast.LENGTH_LONG).show()

                spin_value_worker = optionsWorkers.get(position)
            }
        }

        prof_pic_worker.setOnClickListener {
            askCameraPermission()
        }

        btn_add_Worker.setOnClickListener {
            progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Wait a Sec....Adding Worker Info")
            progressDialog.setCancelable(false)
            progressDialog.show()
            UploadWorkerImgtoFirebase()
        }
    }

    private fun askCameraPermission() {
        askPermission(Manifest.permission.CAMERA){
            takePhotoByCamera()
        }.onDeclined { e ->
            if (e.hasDenied()) {
                //the list of denied permissions
                e.denied.forEach {
                }

                AlertDialog.Builder(this@Add_Worker)
                        .setMessage("Please accept our permissions.. Otherwise you will not be able to use our some of Features.")
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

    private fun dateJoiningWorker() {
        val now = Calendar.getInstance()
        datePickerdialog_worker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(Calendar.YEAR,year)
            selectedDate.set(Calendar.MONTH,month)
            selectedDate.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            val date = formate.format(selectedDate.time)
            Toast.makeText(this,"date : " + date,Toast.LENGTH_SHORT).show()
            date_editText_worker.setText(date)
        },
                now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))
        datePickerdialog_worker.show()
    }

    fun takePhotoByCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAPTURE_PHOTO)
    }

    var imageUriworker  : Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                CAPTURE_PHOTO -> {
                    val capturedBitmap = data!!.extras!!.get("data") as Bitmap
                    circular_worker_profile_pic.setImageBitmap(capturedBitmap)
                    prof_pic_worker.alpha = 0f

                    saveImage(capturedBitmap)
                }

                else -> {
                    Toast.makeText(this,"Failed to Capture Image",Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun saveImage(finalBitmap: Bitmap)
    {
        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File(root + "/SocietyApp/Workers")
        myDir.mkdirs()
        val generator = Random()
        var n = 10000
        n = generator.nextInt(n)
        val OutletFname = "WorkerImg-$n.jpg"
        val file = File(myDir, OutletFname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            imagePath = file.absolutePath
            Log.d("SocietyLogs","Image Path"+imagePath.toString())
            imageUriworker = Uri.fromFile(file)
            Log.d("SocietyLogs","Uri is"+imageUriworker.toString())

            out.flush()
            out.close()
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }



    private fun UploadWorkerImgtoFirebase() {

        if (text_name_worker.text.isEmpty()) {
            text_name_worker.error = "Please Enter Full Name"
            return
        } else if (text_address_worker.text.isEmpty()) {
            text_address_worker.error = "Please Enter Valid Password"
            return
        } else if (text_mobile_worker.text.isEmpty() || text_mobile_worker.text.toString().length < 10) {
            text_mobile_worker.error = " Please Enter Valid Contact Number"
            return
        }

        Log.d("SocietyLogs","Uri is Uplod"+imageUriworker.toString())
        if(imageUriworker == null || date_editText_worker.text.isEmpty())
        {
            progressDialog.dismiss()
            Toast.makeText(applicationContext,"Please Select Valid Image & Valid Data",Toast.LENGTH_LONG).show()
            return
        }


        val Workerfilename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/WorkersProfPic/$Workerfilename")

        ref.putFile(imageUriworker!!)
                .addOnSuccessListener {
                    Toast.makeText(applicationContext,"Image Uploaded",Toast.LENGTH_LONG).show()
                    Log.d("SocietyLogs","Image uploaded")
                    ref.downloadUrl.addOnSuccessListener {
                        it.toString()
                        addWorkertoFirebase(it.toString())
                    }
                }
                .addOnFailureListener {

                }
    }


    private fun addWorkertoFirebase(ImageUrlWorker : String) {

        val recordid = UUID.randomUUID().toString()
        val ref = FirebaseDatabase.getInstance().getReference("/Workers/$recordid")

        val status = AddWorkerClass(recordid,text_name_worker.text.toString(),ImageUrlWorker,
                text_address_worker.text.toString(),spin_value_worker,
                text_mobile_worker.text.toString(),date_editText_worker.text.toString(),edSpeciality.text.toString())
        ref.setValue(status)
                .addOnSuccessListener {

                    progressDialog.dismiss()
                    showAlert()
                    val intent = Intent(this,MainActivity :: class.java)
                    startActivity(intent)
                    sendFCMtoUsers()

                }
                .addOnFailureListener {
                    Alerter.create(this@Add_Worker)
                            .setTitle("Payment Update")
                            .setIcon(R.drawable.alert)
                            .setDuration(4000)
                            .setText("Failed to Update!! Please Try after some time!!")
                            .setBackgroundColorRes(R.color.colorAccent)
                            .show()
                }
    }

    private fun showAlert() {
        Alerter.create(this@Add_Worker)
                .setTitle("Worker")
                .setIcon(R.drawable.worker)
                .setDuration(4000)
                .setText("Worker Added Successfully!! :)")
                .setBackgroundColorRes(R.color.colorAccent)
                .show()
    }

    private fun fetchuserMobilefromFirebase()
    {
        var db = FirebaseFirestore.getInstance()
        db.collection("FlatUsers")
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
                            con.setRequestProperty("Authorization", "Basic Y2Q3ODRhYTUtMjA4ZC00NTZjLTg3MDktMzEwNjJkOWMwMTRi")
                            con.setRequestMethod("POST")

                            val strJsonBody = ("{"
                                    + "\"app_id\": \"69734071-08a8-4d63-a7ab-adda8e2197f0\","

                                    + "\"filters\": [{\"field\": \"tag\", \"key\": \"NotificationID\", \"relation\": \"=\", \"value\": \"" + sendNotificationID + "\"}],"

                                    + "\"data\": {\"foo\": \"bar\"},"
                                    + "\"contents\": {\"en\": \"New Worker Added\"}"
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
