package com.nil_projects.society_app

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.R.drawable
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ramotion.foldingcell.FoldingCell
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.OnItemLongClickListener
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_workers__list.*
import kotlinx.android.synthetic.main.custom_records_layout.view.*
import kotlinx.android.synthetic.main.custom_workser_list.view.*


class Workers_List : AppCompatActivity() {

    lateinit var workerslist_recyclerview: RecyclerView
    lateinit var typeWorker : String
    lateinit var toggleBtn : FoldingCell
    lateinit var fc : FoldingCell
    private val REQUEST_CALL = 1



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workers__list)

        val bundle : Bundle? = intent.extras
        typeWorker = bundle!!.getString("workerType")

        workerslist_recyclerview = findViewById<RecyclerView>(R.id.workers_list_recyclerview)

        fetchWorkers()

    }


    private fun fetchWorkers()
    {
        val ref = FirebaseDatabase.getInstance().getReference("/Workers")
        ref.addListenerForSingleValueEvent(object : ValueEventListener
        {
            val adapter = GroupAdapter<ViewHolder>()
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val worker = it.getValue(FetchWorkerClass :: class.java)

                    Log.d("SocietyLogs",worker!!.name)
                    if(worker != null && worker.type == typeWorker)
                    {
                        adapter.add(WorkerItem(worker))
                    }
                }

                adapter.setOnItemClickListener { item, view ->
                    view.folding_cell.toggle(false)
                    view.custom_callingbtn_worker.setOnClickListener {
                        Toast.makeText(applicationContext,"Calling",Toast.LENGTH_LONG).show()
                        makeCall()
                    }
                }
                workerslist_recyclerview.adapter = adapter
            }
        })
    }

    private fun makeCall() {
        var number = "8446613467"
        if (number.trim({ it <= ' ' }).length > 0) {

            if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.CALL_PHONE
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL
                )
            } else {
                val dial = "tel:$number"
                startActivity(Intent(Intent.ACTION_CALL, Uri.parse(dial)))
            }

        } else {
            Toast.makeText(this, "Enter Phone Number", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeCall()
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

class FetchWorkerClass(val id : String,val name : String,val imageUrl : String,val address : String,
                     val type : String,val mobile : String,val dateofjoining : String)
{
    constructor() : this("","","","","",",","")
}



class WorkerItem(var finalworker : FetchWorkerClass) : Item<ViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.custom_workser_list
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.custom_name_worker.text = finalworker.name
        viewHolder.itemView.custom_type_worker.text = finalworker.type
        Picasso.get().load(finalworker.imageUrl).into(viewHolder.itemView.custom_prof_worker)
        Picasso.get().load(finalworker.imageUrl).into(viewHolder.itemView.photo)
    }
}

