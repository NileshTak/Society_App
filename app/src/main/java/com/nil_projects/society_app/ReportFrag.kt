package com.nil_projects.society_app

import android.content.Intent
import android.os.*

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.tapadoo.alerter.Alerter
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.custom_records_layout.view.*
import kotlin.collections.ArrayList


class ReportFrag : Fragment() {

    lateinit var recyclerview_xml_reportfrag: RecyclerView
    lateinit var swipeRefreshLayoutReportFrag: SwipeRefreshLayout
    //  val adapter = GroupAdapter<ViewHolder>()

    private lateinit var mAdapter: FirestorePagingAdapter<reportModelClass, FetchRecordItem>
    private val mFirestore = FirebaseFirestore.getInstance()
    private val mPostsCollection = mFirestore.collection("Records")
    private val mQuery = mPostsCollection.orderBy("counter", Query.Direction.DESCENDING)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment


        //   fetchRecords( )

        val view = inflater.inflate(R.layout.fragment_report, container, false)

        recyclerview_xml_reportfrag = view.findViewById<RecyclerView>(R.id.recyclerview_xml_reportfrag)
        swipeRefreshLayoutReportFrag = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayoutReportFrag)

        recyclerview_xml_reportfrag.setHasFixedSize(true)

        fetchRecord()

        return view
    }

    private fun fetchRecord() {
        // Init Paging Configuration
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(2)
                .setPageSize(3)
                .build()

        // Init Adapter Configuration
        val options = FirestorePagingOptions.Builder<reportModelClass>()
                .setLifecycleOwner(this)
                .setQuery(mQuery, config, reportModelClass::class.java)
                .build()

        // Instantiate Paging Adapter
        mAdapter = object : FirestorePagingAdapter<reportModelClass, FetchRecordItem>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FetchRecordItem {
                val view = layoutInflater.inflate(R.layout.custom_records_layout, parent, false)
                return FetchRecordItem(view)
            }

            override fun onBindViewHolder(viewHolder: FetchRecordItem, position: Int, post: reportModelClass) {
                // Bind to ViewHolder
                viewHolder.bind(post)
            }


            override fun onLoadingStateChanged(state: LoadingState) {
                when (state) {
                    LoadingState.LOADING_INITIAL -> {
                        swipeRefreshLayoutReportFrag.isRefreshing = true
                    }

                    LoadingState.LOADING_MORE -> {
                        swipeRefreshLayoutReportFrag.isRefreshing = true
                    }

                    LoadingState.LOADED -> {
                        swipeRefreshLayoutReportFrag.isRefreshing = false
                    }

                    LoadingState.ERROR -> {
                        Toast.makeText(
                                activity,
                                "Low Network",
                                Toast.LENGTH_SHORT
                        ).show()
                        swipeRefreshLayoutReportFrag.isRefreshing = false
                    }

                    LoadingState.FINISHED -> {
                        swipeRefreshLayoutReportFrag.isRefreshing = false
                    }
                }
            }

        }

        recyclerview_xml_reportfrag.adapter = mAdapter

    }
}

//    private fun fetchRecords( ) {
//
//
//
//        var db = FirebaseFirestore.getInstance()
//
//        db.collection("Records")
//                .orderBy("counter", Query.Direction.DESCENDING)
//
//                .get()
//                .addOnSuccessListener {
//
//                    it.documents.forEach {
//                        val record = it.toObject(reportModelClass::class.java)
//
//
//                        if (record != null) {
//                            adapter.add(FetchRecordItem(record))
//                        }
//
//
//
//                    }
//                    recyclerview_xml_reportfrag.adapter = adapter
//                }
//                .addOnFailureListener {
//                    Alerter.create(getActivity())
//                            .setTitle("Society Notice")
//                            .setIcon(R.drawable.alert)
//                            .setDuration(4000)
//                            .setText("Failed to Fetch!! Please Try after some time!!")
//                            .setBackgroundColorRes(R.color.colorAccent)
//                            .show()
//                }
//
//         }
//    inner class FetchRecordItem(var Finalrecord : reportModelClass) : Item<ViewHolder>()
//    {
//
//        override fun getLayout(): Int {
//            return R.layout.custom_records_layout
//        }
//
//        override fun bind(viewHolder: ViewHolder, position: Int) {
//            viewHolder.itemView.date_custom_record.text = Finalrecord.date
//            viewHolder.itemView.wing_spinner_value_status.text = Finalrecord.wing
//           // Picasso.get().load(Finalrecord.imageUrl).into(viewHolder.itemView.record_img_xml)
//            Glide.with(activity).load(Finalrecord.imageUrl).into(viewHolder.itemView.record_img_xml)
//
//            viewHolder.itemView.setOnClickListener {
//                var int = Intent(activity,FUllScreenImage :: class.java)
//                int.data = Finalrecord.imageUrl.toUri()
//                int.putExtra("msg",Finalrecord.date)
//                int.putExtra("id",Finalrecord.id)
//                int.putExtra("collectionName","Records")
//                startActivity(int)
//            }
//        }
//    }


class FetchRecordItem(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var date_custom_record: TextView = itemView.findViewById<TextView>(R.id.date_custom_record)
    private var wing_spinner_value_status: TextView = itemView.findViewById<TextView>(R.id.wing_spinner_value_status)
    private var record_img_xml: ImageView = itemView.findViewById<ImageView>(R.id.record_img_xml)


    fun bind(Finalrecord: reportModelClass) {

        date_custom_record.text = Finalrecord.date
         wing_spinner_value_status.text = Finalrecord.wing
           // Picasso.get().load(Finalrecord.imageUrl).into(viewHolder.itemView.record_img_xml)
            Glide.with(record_img_xml.context).load(Finalrecord.imageUrl).into( record_img_xml)

        itemView.setOnClickListener {
                var int = Intent(record_img_xml.context,FUllScreenImage :: class.java)
                int.data = Finalrecord.imageUrl.toUri()
                int.putExtra("msg",Finalrecord.date)
                int.putExtra("id",Finalrecord.id)
            int.putExtra("userid",Finalrecord.userid)
                int.putExtra("collectionName","Records")
            record_img_xml.context.startActivity(int)
            }
    }

}
