package com.nil_projects.society_app

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.SliderViewAdapter

import java.util.ArrayList


class SliderAdapterExample(private val context: Context, private val arr: ArrayList<String>) :
    SliderViewAdapter<SliderAdapterExample.SliderAdapterVH>() {

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflate = LayoutInflater.from(parent.context).inflate(R.layout.image_slider_layout_item, null)
        return SliderAdapterVH(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {

        when (position) {
            0 -> Glide.with(viewHolder.itemView)
                    .load(arr[0]).into(viewHolder.imageViewBackground)
            1 -> Glide.with(viewHolder.itemView)
                    .load(arr[1])
                    .into(viewHolder.imageViewBackground)
            2 -> Glide.with(viewHolder.itemView)
                    .load(arr[2])
                    .into(viewHolder.imageViewBackground)
            3 -> Glide.with(viewHolder.itemView)
                    .load(arr[3])
                    .into(viewHolder.imageViewBackground)
            else -> Glide.with(viewHolder.itemView)
                    .load(arr[0])
                    .into(viewHolder.imageViewBackground)
        }
    }

    override fun getCount(): Int {
        //slider view count could be dynamic size
        return 4
    }

    inner class SliderAdapterVH(internal var itemView: View) : SliderViewAdapter.ViewHolder(itemView) {
        internal var imageViewBackground: ImageView

        init {
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider)
        }
    }
}
