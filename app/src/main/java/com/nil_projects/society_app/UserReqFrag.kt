package com.nil_projects.society_app

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.FragmentStatePagerAdapter
import butterknife.BindView
import butterknife.ButterKnife
import com.github.florent37.materialviewpager.MaterialViewPager
import com.github.florent37.materialviewpager.header.HeaderDesign
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.nil_projects.society_app.fragment.RecyclerViewFragment
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.custom_notification.view.*
import kotlinx.android.synthetic.main.custom_userreq.view.*
import kotlinx.android.synthetic.main.fragment_user_req.*

class UserReqFrag : Fragment() {

    @BindView(R.id.materialViewPager)
    internal var mViewPager: MaterialViewPager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

//        fetchUserRequests()
        val view = inflater.inflate(R.layout.fragment_user_req, container, false)
        ButterKnife.bind(view)
        mViewPager = view.findViewById<MaterialViewPager>(R.id.materialViewPager)

        mViewPager!!.viewPager.adapter = object : FragmentStatePagerAdapter(fragmentManager) {

            override fun getItem(position: Int): Fragment {
                return RecyclerViewFragment.newInstance()
            }

            override fun getCount(): Int {
                return 3
            }

            override fun getPageTitle(position: Int): CharSequence? {
                when (position % 4) {
                    0 -> return "Pending Requests"
                    1 -> return "Approved Requests"
                    2 -> return "Rejected Requests"
                }
                return ""
            }
        }

        mViewPager!!.setMaterialViewPagerListener(MaterialViewPager.Listener { page ->
            when (page) {
                0 -> return@Listener HeaderDesign.fromColorResAndUrl(
                        R.color.green,
                        "http://phandroid.s3.amazonaws.com/wp-content/uploads/2014/06/android_google_moutain_google_now_1920x1080_wallpaper_Wallpaper-HD_2560x1600_www.paperhi.com_-640x400.jpg")
                1 -> return@Listener HeaderDesign.fromColorResAndUrl(
                        R.color.blue,
                        "http://www.hdiphonewallpapers.us/phone-wallpapers/540x960-1/540x960-mobile-wallpapers-hd-2218x5ox3.jpg")
                2 -> return@Listener HeaderDesign.fromColorResAndUrl(
                        R.color.cyan,
                        "http://www.droid-life.com/wp-content/uploads/2014/10/lollipop-wallpapers10.jpg")
                3 -> return@Listener HeaderDesign.fromColorResAndUrl(
                        R.color.red,
                        "http://www.tothemobile.com/wp-content/uploads/2014/07/original.jpg")
            }

            //execute others actions if needed (ex : modify your header logo)

            null
        })

        mViewPager!!.viewPager.offscreenPageLimit = mViewPager!!.viewPager.adapter!!.count
        mViewPager!!.pagerTitleStrip.setViewPager(mViewPager!!.viewPager)

        val logo = view.findViewById<ImageView>(R.id.logo_white)
        if (logo != null) {
            logo!!.setOnClickListener{
                mViewPager!!.notifyHeaderChanged()
                Toast.makeText(activity, "Yes, the title is clickable", Toast.LENGTH_SHORT).show()
            }
        }

        return view

    }


private fun fetchUserRequests()
{
    var db = FirebaseFirestore.getInstance()
    db.collection("FlatUsers")
            .whereEqualTo("userAuth", "Pending")
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val adapter = GroupAdapter<ViewHolder>()
                val city = documentSnapshot.toObjects(UserReqClass :: class.java)
                for (document in city) {
                        adapter.add(FetchUserReqItem(document))
                }
                //recyclerview_xml_userReqfrag.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Log.w("SocietyFirestore", "Error getting documents.", exception)
            }
}
}



class FetchUserReqItem(var Finalreq : UserReqClass) : Item<ViewHolder>()
{

    override fun getLayout(): Int {
        return R.layout.custom_userreq
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.cutom_userreq_name.text = Finalreq.UserName
        viewHolder.itemView.cutom_userreq_flatno.text = Finalreq.FlatNo
        viewHolder.itemView.cutom_userreq_wingname.text = Finalreq.Wing
        viewHolder.itemView.cutom_userreq_relation.text = Finalreq.UserRelation
        viewHolder.itemView.cutom_userreq_email.text = Finalreq.UserEmail
    }
}


class UserReqClass(val id : String,val profile_Pic_url : String,val UserName : String,val UserEmail : String,val pass : String,
                val city: String,val societyname : String,val Wing : String,
                val FlatNo : String,val UserRelation : String,val userAuth : String)
{
    constructor() : this("","","","","","","","","","","")
}