package com.nil_projects.society_app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
    lateinit var swipeRefreshLayoutNotifiFrag : SwipeRefreshLayout
  //  var lastResult : DocumentSnapshot? = null
  //  val adapter = GroupAdapter<ViewHolder>()

   // var db = FirebaseFirestore.getInstance().collection("Notifications")

    private lateinit var mAdapter: FirestorePagingAdapter<AddNotifiClass, FetchNotificationItem>
    private val mFirestore = FirebaseFirestore.getInstance()
    private val mPostsCollection = mFirestore.collection("Notifications")
    private val mQuery = mPostsCollection.orderBy("counter", Query.Direction.DESCENDING)




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_notification, container, false)

        recyclerview_xml_notifrag = view.findViewById<RecyclerView>(R.id.recyclerview_xml_notifrag)
        swipeRefreshLayoutNotifiFrag = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayoutNotifiFrag)


        recyclerview_xml_notifrag.setHasFixedSize(true)

        setupAdapter()


//        fetchNotifications()

        return view
    }


    private fun setupAdapter() {

        // Init Paging Configuration
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(2)
                .setPageSize(3)
                .build()

        // Init Adapter Configuration
        val options = FirestorePagingOptions.Builder<AddNotifiClass>()
                .setLifecycleOwner(this)
                .setQuery(mQuery, config, AddNotifiClass::class.java)
                .build()

        // Instantiate Paging Adapter
        mAdapter = object : FirestorePagingAdapter<AddNotifiClass, FetchNotificationItem>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FetchNotificationItem {
                val view = layoutInflater.inflate(R.layout.custom_notification, parent, false)
                return FetchNotificationItem(view)
            }

            override fun onBindViewHolder(viewHolder: FetchNotificationItem, position: Int, post: AddNotifiClass) {
                // Bind to ViewHolder
                viewHolder.bind(post)
            }


            override fun onLoadingStateChanged(state: LoadingState) {
                when (state) {
                    LoadingState.LOADING_INITIAL -> {
                        swipeRefreshLayoutNotifiFrag.isRefreshing = true
                    }

                    LoadingState.LOADING_MORE -> {
                        swipeRefreshLayoutNotifiFrag.isRefreshing = true
                    }

                    LoadingState.LOADED -> {
                        swipeRefreshLayoutNotifiFrag.isRefreshing = false
                    }

                    LoadingState.ERROR -> {
                        Toast.makeText(
                                activity,
                                "Low Network",
                                Toast.LENGTH_SHORT
                        ).show()
                        swipeRefreshLayoutNotifiFrag.isRefreshing = false
                    }

                    LoadingState.FINISHED -> {
                        swipeRefreshLayoutNotifiFrag.isRefreshing = false
                    }
                }
            }

        }

        recyclerview_xml_notifrag.adapter = mAdapter

    }

//    private fun fetchNotifications() {
//
//   db.orderBy("counter", Query.Direction.DESCENDING)
//                .get()
//                .addOnSuccessListener {
//
//                     it.documents.forEach {
//                        val record = it.toObject(AddNotifiClass::class.java)
//
//                        if (record != null) {
//
//                            adapter.add(FetchNotificationItem(record))
//
//                        }
//
//                    }
//
//                         recyclerview_xml_notifrag.adapter = adapter
//
//                }

    }

//    inner class FetchNotificationItem(var Finalnotifi : AddNotifiClass) : Item<ViewHolder>()
//    {
//
//        override fun getLayout(): Int {
//            return R.layout.custom_notification
//        }
//
//        override fun bind(viewHolder: ViewHolder, position: Int) {
//
//            viewHolder.itemView.noti_text_view.text = Finalnotifi.noti
//            viewHolder.itemView.currentTimeRecord.text = Finalnotifi.currentTime
//         //   Picasso.get().load(Finalnotifi.imageUrl).into(viewHolder.itemView.noti_img_xml)
//            Glide.with(activity).load(Finalnotifi.imageUrl).into(viewHolder.itemView.noti_img_xml)
//
//
//            viewHolder.itemView.setOnClickListener {
//                var int = Intent(activity,FUllScreenImage :: class.java)
//                int.data = Finalnotifi.imageUrl.toUri()
//                int.putExtra("msg",Finalnotifi.noti)
//                int.putExtra("msg",Finalnotifi.noti)
//                int.putExtra("id",Finalnotifi.id)
//                int.putExtra("collectionName","Notifications")
//                startActivity(int)
//            }
//        }
//    }



class FetchNotificationItem(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var notiText: TextView = itemView.findViewById<TextView>(R.id.noti_text_view)
    private var notiTime: TextView = itemView.findViewById<TextView>(R.id.currentTimeRecord)
    private var notiImage: ImageView = itemView.findViewById<ImageView>(R.id.noti_img_xml)


    fun bind(Finalnotifi: AddNotifiClass) {

        notiText.text = Finalnotifi.noti
        notiTime.text = Finalnotifi.currentTime
         //   Picasso.get().load(Finalnotifi.imageUrl).into(viewHolder.itemView.noti_img_xml)
            Glide.with(notiImage.context ).load(Finalnotifi.imageUrl).into(notiImage )


            notiImage.setOnClickListener {
                var int = Intent(notiImage.context,FUllScreenImage :: class.java)
                int.data = Finalnotifi.imageUrl.toUri()
                int.putExtra("msg",Finalnotifi.noti)
                int.putExtra("msg",Finalnotifi.noti)
                int.putExtra("id",Finalnotifi.id)
                int.putExtra("collectionName","Notifications")
                notiImage.context.startActivity(int)
            }
    }

}
