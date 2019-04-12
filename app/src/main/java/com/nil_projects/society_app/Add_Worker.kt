package com.nil_projects.society_app

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add__worker.*
import kotlinx.android.synthetic.main.fragment_report.*
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add__worker)
        supportActionBar!!.title = "Add Worker"

        spinner_type_worker = findViewById<Spinner>(R.id.spinner_type_worker)
        btn_add_Worker = findViewById<Button>(R.id.btn_add_worker)
        date_editText_worker = findViewById<EditText>(R.id.date_worker_joining)


        date_editText_worker.setOnClickListener {
            dateJoiningWorker()
        }

        val optionsWorkers = arrayOf("Cook", "Driver","Security Guard","Car Cleaner")

        spinner_type_worker.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,optionsWorkers)
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (PermissionChecker.checkSelfPermission(applicationContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
                }

            }
            if (ActivityCompat.checkSelfPermission(applicationContext,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERM_WRITE_STORAGE)

            } else {
                takePhotoByCamera()
            }

        }

        btn_add_Worker.setOnClickListener {
            progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Wait a Sec....Adding Worker Info")
            progressDialog.setCancelable(false)
            progressDialog.show()
            UploadWorkerImgtoFirebase()
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
        val myDir = File(root + "/Society_App_Records/Workers")
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
                text_mobile_worker.text.toString(),date_editText_worker.text.toString())
        ref.setValue(status)
                .addOnSuccessListener {

                    progressDialog.dismiss()
                    Toast.makeText(applicationContext,"Worker Added Successfully",Toast.LENGTH_LONG).show()
                    val intent = Intent(this,MainActivity :: class.java)
                    startActivity(intent)

                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext,"Failed to Add Worker",Toast.LENGTH_LONG).show()
                }
    }


}

class AddWorkerClass(val id : String,val name : String,val imageUrl : String,val address : String,
                     val type : String,val mobile : String,val dateofjoining : String)
{
    constructor() : this("","","","","",",","")
}
