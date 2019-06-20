package com.nil_projects.society_app

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.PendingIntent.getActivity
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*

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
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.flags.impl.DataUtils
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.OnItemLongClickListener
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.custom_records_layout.*
import kotlinx.android.synthetic.main.custom_records_layout.view.*
import kotlinx.android.synthetic.main.fragment_report.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class ReportFrag : Fragment() {

    lateinit var recyclerview_xml_reportfrag : RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        fetchRecords()

        val view = inflater.inflate(R.layout.fragment_report, container, false)

        recyclerview_xml_reportfrag = view.findViewById<RecyclerView>(R.id.recyclerview_xml_reportfrag)

        return view
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
    inner class FetchRecordItem(var Finalrecord : RecordClass) : Item<ViewHolder>()
    {

        override fun getLayout(): Int {
            return R.layout.custom_records_layout
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.date_custom_record.text = Finalrecord.date
            viewHolder.itemView.wing_spinner_value_status.text = Finalrecord.wing
           // Picasso.get().load(Finalrecord.imageUrl).into(viewHolder.itemView.record_img_xml)
            Glide.with(activity).load(Finalrecord.imageUrl).into(viewHolder.itemView.record_img_xml)

            viewHolder.itemView.setOnClickListener {
                var int = Intent(activity,FUllScreenImage :: class.java)
                int.data = Finalrecord.imageUrl.toUri()
                int.putExtra("msg",Finalrecord.date)
                startActivity(int)
            }
        }
    }
}



class RecordClass(val id : String,val date: String,val imageUrl : String,val counter : String,val wing : String)
{
    constructor() : this("","","","","")
}

