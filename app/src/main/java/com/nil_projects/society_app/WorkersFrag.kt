package com.nil_projects.society_app

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nil_projects.viewpager.NatureCreativePagerAdapter
import com.tbuonomo.creativeviewpager.CreativeViewPager


class WorkersFrag : Fragment() {

    lateinit var creativeViewPagerView : CreativeViewPager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_workers, container, false)

        creativeViewPagerView = view.findViewById<CreativeViewPager>(R.id.creativeViewPagerView)

        creativeViewPagerView.setCreativeViewPagerAdapter(NatureCreativePagerAdapter(activity!!.applicationContext))

        return view
    }
}