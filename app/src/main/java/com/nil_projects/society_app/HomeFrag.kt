package com.nil_projects.society_app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.nil_projects.society_app.fragment.Model
import com.smarteist.autoimageslider.IndicatorAnimations
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.OnItemLongClickListener
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.custom_layout_last.view.*
import kotlinx.android.synthetic.main.custom_layout_middle.view.*
import kotlinx.android.synthetic.main.custom_layout_workerhome.view.*
import kotlinx.android.synthetic.main.custom_userreq.view.*
import kotlinx.android.synthetic.main.customhomependingreq.view.*
import kotlinx.android.synthetic.main.customhomependingreq.view.cutom_userreq_flatno
import kotlinx.android.synthetic.main.customhomependingreq.view.cutom_userreq_name
import kotlinx.android.synthetic.main.customhomependingreq.view.cutom_userreq_number
import kotlinx.android.synthetic.main.customhomependingreq.view.cutom_userreq_relation

import java.util.ArrayList


class HomeFrag : Fragment() {

    lateinit var sliderView: SliderView
    lateinit var arr : ArrayList<String>
    lateinit var first_recycler : RecyclerView
    lateinit var second_recycler : RecyclerView
    lateinit var workers_recycler : RecyclerView
    lateinit var pendingReqRecycler : RecyclerView
    lateinit var tvPendingreq : TextView
    lateinit var tvSocietyNotice : TextView
    lateinit var tvBuildingNotice : TextView
    lateinit var tvWorker : TextView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        pendingReqRecycler = view.findViewById<RecyclerView>(R.id.pendingReqRecycler)
        first_recycler = view.findViewById<RecyclerView>(R.id.first_recycler)
        second_recycler = view.findViewById<RecyclerView>(R.id.second_recycler)
        workers_recycler = view.findViewById<RecyclerView>(R.id.workers_recycler)
        sliderView = view.findViewById<SliderView>(R.id.imageSlider)
        tvPendingreq = view.findViewById<TextView>(R.id.tvPendingreq)
        tvSocietyNotice = view.findViewById<TextView>(R.id.tvSocietyNotice)
        tvBuildingNotice = view.findViewById<TextView>(R.id.tvBuildingNotice)
        tvWorker = view.findViewById<TextView>(R.id.tvWorker)

        fetchSliderImages()
        fetchpendingReqRecycler()
        fetchRecords()
        fetchNotifications()
        fetchWorkers()

        return view
    }

    private fun fetchpendingReqRecycler() {
        val adapter = GroupAdapter<ViewHolder>()

        var db = FirebaseFirestore.getInstance()
        db.collection("FlatUsers")
                .whereEqualTo("userAuth", "Pending")
                .get()
                .addOnSuccessListener {
                    var data = it.toObjects(Model :: class.java)
                    for(doc in data)
                    {
                        if(doc != null)
                        {
                            tvPendingreq.visibility = View.VISIBLE
                            adapter.add(FetchPendingReq(doc))
                        }
                    }
                    pendingReqRecycler.adapter = adapter
                }
    }


    inner class FetchPendingReq(var Finaldata : Model) : Item<ViewHolder>()
    {

        override fun getLayout(): Int {
            return R.layout.customhomependingreq
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.cutom_userreq_flatno.text = Finaldata.flatNo
            viewHolder.itemView.cutom_userreq_name.text = Finaldata.userName
            viewHolder.itemView.cutom_userreq_number.text = Finaldata.mobileNumber
            viewHolder.itemView.cutom_userreq_relation.text = Finaldata.userRelation
        }
    }

    private fun fetchSliderImages() {
        var db = FirebaseFirestore.getInstance()
        db.collection("UiHome").document("Images")
                .get()
                .addOnSuccessListener { documentSnapshot ->

                    val city = documentSnapshot.toObject(SliderImgClass :: class.java)
                    if (city != null) {
                        arr = arrayListOf<String>()
                        arr.add(city.Img1)
                        arr.add(city.Img2)
                        arr.add(city.Img3)
                        arr.add(city.Img4)
                        arr.add(city.Img5)

                        val adapter = SliderAdapterExample(activity!!.applicationContext,arr)
                        sliderView.startAutoCycle()
                        sliderView.sliderAdapter = adapter

                        sliderView.setIndicatorAnimation(IndicatorAnimations.THIN_WORM) //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                        sliderView.sliderIndicatorSelectedColor = Color.WHITE
                        sliderView.sliderIndicatorUnselectedColor = Color.GRAY
                        sliderView.setSliderTransformAnimation(SliderAnimations.CUBEOUTDEPTHTRANSFORMATION)
                        sliderView.scrollTimeInSec = 4 //set scroll delay in seconds :

                        sliderView.setOnIndicatorClickListener { position -> sliderView.currentPagePosition = position }

                    }
                }

                .addOnFailureListener { exception ->
                    Log.w("SocietyFirestore", "Error getting documents.", exception)
                }
    }

    private fun fetchRecords() {
        val adapter = GroupAdapter<ViewHolder>()

        val ref = FirebaseDatabase.getInstance().getReference("/RecordsDates")
        var recordsorder = ref.orderByChild("counter")
        ref.addListenerForSingleValueEvent(object : ValueEventListener
        {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    recordsorder
                    val record = it.getValue(RecordClass::class.java)

                    if (record != null) {
                        tvBuildingNotice.visibility = View.VISIBLE
                        adapter.add(FetchRecordItemHome(record))
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


                first_recycler.adapter = adapter
            }
        })
    }

    inner class FetchRecordItemHome(var Finalrecord : RecordClass) : Item<ViewHolder>()
    {

        override fun getLayout(): Int {
            return R.layout.custom_layout_middle
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            Glide.with(activity).load(Finalrecord.imageUrl).into(viewHolder.itemView.img_view_custom_middle)

            viewHolder.itemView.setOnClickListener {
                var int = Intent(activity,FUllScreenImage :: class.java)
                int.data = Finalrecord.imageUrl.toUri()
                int.putExtra("msg",Finalrecord.date)
                startActivity(int)
            }
        }
    }

    private fun fetchNotifications() {
        val ref = FirebaseDatabase.getInstance().getReference("/Notifications")
        ref.addListenerForSingleValueEvent(object : ValueEventListener
        {
            val adapter = GroupAdapter<ViewHolder>()

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val notifi = it.getValue(AddNotifiClass::class.java)

                    if (notifi != null) {
                        tvSocietyNotice.visibility = View.VISIBLE
                        adapter.add(FetchNotificationItemHome(notifi))
                    }
                }
                second_recycler.adapter = adapter
            }
        })
    }

    inner class FetchNotificationItemHome(var Finalnotifi : AddNotifiClass) : Item<ViewHolder>()
    {

        override fun getLayout(): Int {
            return R.layout.custom_layout_last
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {

           // viewHolder.itemView.noti_text_view.text = Finalnotifi.noti
            //   Picasso.get().load(Finalnotifi.imageUrl).into(viewHolder.itemView.noti_img_xml)
            Glide.with(activity).load(Finalnotifi.imageUrl).into(viewHolder.itemView.img_view_custom_last)


            viewHolder.itemView.setOnClickListener {
                var int = Intent(activity,FUllScreenImage :: class.java)
                int.data = Finalnotifi.imageUrl.toUri()
                int.putExtra("msg",Finalnotifi.noti)
                startActivity(int)
            }
        }
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
                    if(worker != null)
                    {
                        tvWorker.visibility = View.VISIBLE
                        adapter.add(WorkerItemHome(worker))
                    }
                }
                workers_recycler.adapter = adapter
            }
        })
    }

    inner class WorkerItemHome(var finalworker : FetchWorkerClass) : Item<ViewHolder>() {

        override fun getLayout(): Int {
            return R.layout.custom_layout_workerhome
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.worker_name_home.text = finalworker.name
            viewHolder.itemView.worker_type_home.text = "("+finalworker.type+")"
            Glide.with(activity).load(finalworker.imageUrl).into(viewHolder.itemView.img_custom_workerhome)

        }
    }
}

class SliderImgClass(val Img1 : String,val Img2: String,val Img3 : String,val Img4 : String,val Img5 : String)
{
    constructor() : this("","","","","")
}