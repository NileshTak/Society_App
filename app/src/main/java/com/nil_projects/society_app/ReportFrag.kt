package com.nil_projects.society_app

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.OnItemLongClickListener
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.custom_records_layout.*
import kotlinx.android.synthetic.main.custom_records_layout.view.*
import kotlinx.android.synthetic.main.fragment_report.*
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*


class ReportFrag : Fragment() {

    lateinit var img_select_camera: Button
    lateinit var spinner_wing : Spinner
    lateinit var recyclerview_xml_reportfrag : RecyclerView
    val REQUEST_PERM_WRITE_STORAGE = 102
    lateinit var datePickerdialog : DatePickerDialog
    private val CAPTURE_PHOTO = 104
    internal var imagePath: String? = ""
    var formate = SimpleDateFormat("dd MMM, yyyy",Locale.US)
    lateinit var progressDialog: ProgressDialog
    var counter : Long = 0
    var spin_value : String = "Wing"


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        fetchRecords()

        val view = inflater.inflate(R.layout.fragment_report, container, false)

        img_select_camera = view.findViewById<Button>(R.id.img_select_camera)
        var btn_update = view.findViewById<Button>(R.id.btn_update)
        spinner_wing = view.findViewById<Spinner>(R.id.spinner_wing)
        var date_editText = view.findViewById<EditText>(R.id.date_editText)
        recyclerview_xml_reportfrag = view.findViewById<RecyclerView>(R.id.recyclerview_xml_reportfrag)

        date_editText.setOnClickListener {
            datePicker()
        }

        val optionsWings = arrayOf("MADHUMALTI BUILDING", "ROW HOUSE","ABOLI BUILDING","NISHIGANDHA BUILDING","SAYALI BUIDLING","SONCHAFA BUILDING")

        spinner_wing.adapter = ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1,optionsWings)
        spinner_wing.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(activity,"Please Select Society Wing",Toast.LENGTH_LONG).show()
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Toast.makeText(activity,"Selected Society Wing is : "+optionsWings.get(position),Toast.LENGTH_LONG).show()

                spin_value = optionsWings.get(position)
            }
        }


        img_select_camera.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(activity!!.applicationContext,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity!!.parent, arrayOf(Manifest.permission.CAMERA), 1)
                }

            }
            if (ActivityCompat.checkSelfPermission(activity!!.applicationContext,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(activity!!,
                        arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERM_WRITE_STORAGE)

            } else {
                takePhotoByCamera()
            }
        }

        btn_update.setOnClickListener {
            progressDialog = ProgressDialog(activity)
            progressDialog.setMessage("Wait a Sec....Posting Your Record")
            progressDialog.setCancelable(false)
            progressDialog.show()

            UploadImgtoFirebase()
        }
        return view
    }

    private fun datePicker() {
        val now = Calendar.getInstance()
            datePickerdialog = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(Calendar.YEAR,year)
                selectedDate.set(Calendar.MONTH,month)
                selectedDate.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                val date = formate.format(selectedDate.time)
                Toast.makeText(activity,"date : " + date,Toast.LENGTH_SHORT).show()
                date_editText.setText(date)
            },
                    now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))
            datePickerdialog.show()
    }


        fun takePhotoByCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAPTURE_PHOTO)
    }

    var imageUri  : Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, returnIntent: Intent) {
        super.onActivityResult(requestCode, resultCode, returnIntent)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                CAPTURE_PHOTO -> {

                    val capturedBitmap = returnIntent.extras!!.get("data") as Bitmap

                    saveImage(capturedBitmap)
                }

                else -> {
                    Toast.makeText(activity,"Failed to Capture Image",Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun saveImage(finalBitmap: Bitmap)
    {
        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File(root + "/Society_App_Records")
        myDir.mkdirs()
        val generator = Random()
        var n = 10000
        n = generator.nextInt(n)
        val OutletFname = "RecordImg-$n.jpg"
        val file = File(myDir, OutletFname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            imagePath = file.absolutePath
            Log.d("SocietyLogs","Image Path"+imagePath.toString())
            imageUri = Uri.fromFile(file)
            Log.d("SocietyLogs","Uri is"+imageUri.toString())


            out.flush()
            out.close()
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun UploadImgtoFirebase() {
        Log.d("SocietyLogs","Uri is Uplod"+imageUri.toString())
        if(imageUri == null || date_editText.text.isEmpty())
        {
            progressDialog.dismiss()
            Toast.makeText(activity,"Please Select Valid Image & Valid Data",Toast.LENGTH_LONG).show()
            return
        }


        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/RecordImages/$filename")

        ref.putFile(imageUri!!)
                .addOnSuccessListener {
                    Toast.makeText(activity,"Image Uploaded",Toast.LENGTH_LONG).show()
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

        refrec.addValueEventListener(object : ValueEventListener{
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
                    fetchRecords()
                    progressDialog.dismiss()
                    Toast.makeText(activity,"Record Uploaded",Toast.LENGTH_LONG).show()

                }
                .addOnFailureListener {
                    Toast.makeText(activity,"Failed to Save Record",Toast.LENGTH_LONG).show()
                }
    }



    private fun fetchRecords() {
        val ref = FirebaseDatabase.getInstance().getReference("/RecordsDates")
        var recordsorder = ref.orderByChild("counter")
        ref.addListenerForSingleValueEvent(object : ValueEventListener
        {
            val adapter = GroupAdapter<ViewHolder>()

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    recordsorder
                    val record = it.getValue(RecordClass::class.java)

                    if (record != null) {
                        adapter.add(FetchRecordItem(record))
                    }


                    adapter.setOnItemLongClickListener(object : OnItemLongClickListener
                    {
                        override fun onItemLongClick(item: Item<*>, view: View): Boolean
                        {
                            val refChild = FirebaseDatabase.getInstance().getReference("/RecordsDates").child(record!!.id)

                            var popup = PopupMenu(activity,view)
                            popup.menuInflater.inflate(R.menu.status_option,popup.menu)
                            popup.show()

                            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener{
                                override fun onMenuItemClick(item: MenuItem?): Boolean {

                                    when (item!!.title)
                                    {
                                        "Delete" ->
                                        {
                                            //Toast.makeText(activity,"Deleted",Toast.LENGTH_LONG).show()
                                            refChild.removeValue()
                                            fetchRecords()
                                        }
                                    }
                                    return true
                                }
                            })
                            return true
                        }
                    })
                }


                recyclerview_xml_reportfrag.adapter = adapter
                }
            })
        }
    }



class RecordClass(val id : String,val date: String,val imageUrl : String,val counter : String,val wing : String)
{
    constructor() : this("","","","","")
}

class FetchRecordItem(var Finalrecord : RecordClass) : Item<ViewHolder>()
{

    override fun getLayout(): Int {
        return R.layout.custom_records_layout
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.date_custom_record.text = Finalrecord.date
        viewHolder.itemView.wing_spinner_value_status.text = Finalrecord.wing
        Picasso.get().load(Finalrecord.imageUrl).into(viewHolder.itemView.record_img_xml)
    }
}