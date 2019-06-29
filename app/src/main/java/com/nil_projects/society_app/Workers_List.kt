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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ramotion.foldingcell.FoldingCell
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.OnItemLongClickListener
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_workers__list.*
import kotlinx.android.synthetic.main.custom_records_layout.view.*
import kotlinx.android.synthetic.main.custom_user_profile_list.view.*
import kotlinx.android.synthetic.main.custom_workser_list.view.*
import org.w3c.dom.Text


class Workers_List : AppCompatActivity() {

    lateinit var workerslist_recyclerview: RecyclerView
    lateinit var typeWorker : String
    lateinit var toggleBtn : FoldingCell
    lateinit var fc : FoldingCell
    lateinit var workerNum : String
    private val REQUEST_CALL = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workers__list)
        supportActionBar!!.title = "Workers"
        val actionbar = supportActionBar
        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar!!.setDisplayHomeAsUpEnabled(true)

        val bundle : Bundle? = intent.extras
        typeWorker = bundle!!.getString("workerType")

        workerslist_recyclerview = findViewById<RecyclerView>(R.id.workers_list_recyclerview)

        fetchWorkers()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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
                workerslist_recyclerview.adapter = adapter
            }
        })
    }

    private fun makeCall(number : String) {
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

    inner class WorkerItem(var finalworker : FetchWorkerClass) : Item<ViewHolder>() {

        override fun getLayout(): Int {
            return R.layout.custom_workser_list
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.custom_name_worker.text = finalworker.name
            viewHolder.itemView.custom_type_worker.text = finalworker.type
            viewHolder.itemView.tvWorkerType.text = "("+finalworker.type+")"
            viewHolder.itemView.tvWorkerName.text = finalworker.name
            viewHolder.itemView.tvWorkerAddress.text = finalworker.address
            viewHolder.itemView.tvWorkerDOJ.text = finalworker.dateofjoining
            viewHolder.itemView.tvWorkerNumber.text = finalworker.mobile
            viewHolder.itemView.tvWorkerSpeciality.text = finalworker.speciality
            Glide.with(this@Workers_List).load(finalworker.imageUrl).into(viewHolder.itemView.custom_prof_worker)
            Glide.with(this@Workers_List).load(finalworker.imageUrl).into(viewHolder.itemView.photo)

            workerNum = finalworker.mobile

            viewHolder.itemView.setOnClickListener {
                viewHolder.itemView.folding_cell.toggle(false)
            }
            viewHolder.itemView.custom_callingbtn_worker.setOnClickListener {
                askCallingPermission()
            }
        }

        private fun askCallingPermission() {
                askPermission(Manifest.permission.CALL_PHONE,Manifest.permission.READ_CONTACTS,
                        Manifest.permission.GET_ACCOUNTS){
                    Toast.makeText(applicationContext,"Calling"+finalworker.mobile,Toast.LENGTH_LONG).show()
                    makeCall(finalworker.mobile)
                }.onDeclined { e ->
                    if (e.hasDenied()) {
                        //the list of denied permissions
                        e.denied.forEach {
                        }

                        AlertDialog.Builder(this@Workers_List)
                                .setMessage("Please accept our permissions.. Otherwise you will not be able to use some of our Important Features.")
                                .setPositiveButton("yes") { dialog, which ->
                                    e.askAgain()
                                } //ask again
                                .setNegativeButton("no") { dialog, which ->
                                    dialog.dismiss()
                                }
                                .show()
                    }

                    if (e.hasForeverDenied()) {
                        //the list of forever denied permissions, user has check 'never ask again'
                        e.foreverDenied.forEach {
                        }
                        // you need to open setting manually if you really need it
                        e.goToSettings();
                    }
                }
            }
    }
}






