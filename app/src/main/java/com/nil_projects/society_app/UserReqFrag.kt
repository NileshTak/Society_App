package com.nil_projects.society_app

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
import com.google.firebase.firestore.FirebaseFirestore
import com.nil_projects.society_app.fragment.AcceptedFragment
import com.nil_projects.society_app.fragment.RecyclerViewFragment
import com.nil_projects.society_app.fragment.RejectedFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.custom_userreq.view.*

class UserReqFrag : Fragment() {

    @BindView(R.id.materialViewPager)
    internal var mViewPager: MaterialViewPager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_user_req, container, false)
        ButterKnife.bind(view)
        mViewPager = view.findViewById<MaterialViewPager>(R.id.materialViewPager)

        mViewPager!!.viewPager.adapter = object : FragmentStatePagerAdapter(fragmentManager) {

            override fun getItem(position: Int): Fragment {
                when(position)
                {
                    0 -> return RecyclerViewFragment.newInstance()
                    1 -> return AcceptedFragment.newInstance()
                    2 -> return RejectedFragment.newInstance()
                }
                return Fragment()
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
           null
        })

        mViewPager!!.viewPager.offscreenPageLimit = mViewPager!!.viewPager.adapter!!.count
        mViewPager!!.pagerTitleStrip.setViewPager(mViewPager!!.viewPager)

        val logo = view.findViewById<ImageView>(R.id.logo_white)
        if (logo != null) {
            logo!!.setOnClickListener {
                mViewPager!!.notifyHeaderChanged()
                Toast.makeText(getActivity(), "Yes, the title is clickable", Toast.LENGTH_SHORT).show()
            }
        }

        return view

    }
}