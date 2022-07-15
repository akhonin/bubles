package com.galaxy.bubbles.ui.view.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.galaxy.bubbles.R
import com.galaxy.bubbles.data.FolderItem
import com.galaxy.bubbles.data.MediaItem

class MediaGalleryAdapter(
    private val context: Context
) :
    BaseAdapter() {

    private var layoutInflater: LayoutInflater? = null

    var onItemClick: ((Int) -> Unit)? = null

    var courseList: ArrayList<MediaItem> = ArrayList()

    override fun getCount(): Int {
        return courseList.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView
        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if (convertView == null) {
            convertView = layoutInflater!!.inflate(R.layout.layout_gallery_item, null)
            Glide.with(context)
                .load(courseList[position].FullPath)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(convertView.findViewById(R.id.image))

            convertView.setOnClickListener {
                onItemClick?.invoke(position)
            }
        }

        return convertView
    }
}