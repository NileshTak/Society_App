package com.nil_projects.society_app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.OnItemLongClickListener
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.custom_notification.view.*
import kotlinx.android.synthetic.main.custom_records_layout.view.*
import kotlinx.android.synthetic.main.custom_workser_list.view.*
import kotlinx.android.synthetic.main.fragment_notification.*
import java.io.File

class NotificationFrag : Fragment() {

    lateinit var recyclerview_xml_notifrag : RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_notification, container, false)

        recyclerview_xml_notifrag = view.findViewById<RecyclerView>(R.id.recyclerview_xml_notifrag)

        fetchNotifications()

        return view
    }


    private fun fetchNotifications() {
        var ref = FirebaseDatabase.getInstance().reference.child("Notifications")

        var Notiorder = ref.orderByChild("counter")

        ref.addListenerForSingleValueEvent(object : ValueEventListener
        {
            val adapter = GroupAdapter<ViewHolder>()

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    Notiorder
                    val notifi = it.getValue(AddNotifiClass::class.java)

                    if (notifi != null) {
                        adapter.add(FetchNotificationItem(notifi))
                    }
                }
                recyclerview_xml_notifrag.adapter = adapter
            }
        })
    }

    inner class FetchNotificationItem(var Finalnotifi : AddNotifiClass) : Item<ViewHolder>()
    {

        override fun getLayout(): Int {
            return R.layout.custom_notification
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {

            viewHolder.itemView.noti_text_view.text = Finalnotifi.noti
            viewHolder.itemView.currentTimeRecord.text = Finalnotifi.currentTime
         //   Picasso.get().load(Finalnotifi.imageUrl).into(viewHolder.itemView.noti_img_xml)
            Glide.with(activity).load(Finalnotifi.imageUrl).into(viewHolder.itemView.noti_img_xml)


            viewHolder.itemView.setOnClickListener {
                var int = Intent(activity,FUllScreenImage :: class.java)
                int.data = Finalnotifi.imageUrl.toUri()
                int.putExtra("msg",Finalnotifi.noti)
                int.putExtra("msg",Finalnotifi.noti)
                startActivity(int)
            }
        }
    }
}


