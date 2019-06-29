package com.nil_projects.viewpager

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.nil_projects.society_app.NatureItem
import com.nil_projects.society_app.R
import com.nil_projects.society_app.Workers_List
import com.tbuonomo.creativeviewpager.adapter.CreativePagerAdapter

class NatureCreativePagerAdapter(val context: Context) : CreativePagerAdapter {

  override fun instantiateHeaderItem(inflater: LayoutInflater,
          container: ViewGroup, position: Int): View {
    // Inflate page layout
    val headerRoot = inflater.inflate(R.layout.item_creative_content_nature, container, false)

    // Bind the views
    val title: TextView = headerRoot.findViewById(R.id.itemCreativeNatureTitle)
    val image: ImageView = headerRoot.findViewById(R.id.itemCreativeNatureImage)
    val btn_worker : Button = headerRoot.findViewById(R.id.btn_open_worker)

    title.text = NatureItem.values()[position].title
    btn_worker.setText("View ${NatureItem.values()[position].title} Section")
    image.setImageDrawable(ContextCompat.getDrawable(context, NatureItem.values()[position].natureDrawable))

    btn_worker.setOnClickListener {
      if (btn_worker.text == "View Cooks Section")
      {
        val intent = Intent(context,Workers_List :: class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("workerType","Cook")
        context.startActivity(intent)
      }
      else if (btn_worker.text == "View Drivers Section")
      {
        val intent = Intent(context,Workers_List :: class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("workerType","Driver")
        context.startActivity(intent)
      }
      else if (btn_worker.text == "View Security Guards Section")
      {
        val intent = Intent(context,Workers_List :: class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("workerType","Security Guard")
        context.startActivity(intent)
      }
    }

    return headerRoot
  }

  override fun instantiateContentItem(inflater: LayoutInflater,
          container: ViewGroup, position: Int): View {
    // Inflate the header view layout
    val contentRoot = inflater.inflate(R.layout.item_creative_header_profile, container,
            false)

    // Bind the views
    val imageView = contentRoot.findViewById<ImageView>(R.id.itemCreativeImage)

    imageView.setImageDrawable(
            ContextCompat.getDrawable(context, NatureItem.values()[position].userDrawable))
    return contentRoot
  }

  override fun getCount(): Int {
    return NatureItem.values().size
  }

  override fun isUpdatingBackgroundColor(): Boolean {
    return true
  }

  override fun requestBitmapAtPosition(position: Int): Bitmap? {
    // Return the bitmap used for the position
    return BitmapFactory.decodeResource(context.resources,
            NatureItem.values()[position].natureDrawable)
  }
}