package com.quick.junk.cleaner.adblock.best.free.app.ui.view.adapter

import android.R.attr.path
import android.content.Context
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.quick.junk.cleaner.adblock.best.free.app.R
import com.quick.junk.cleaner.adblock.best.free.app.data.FolderItem


class MediaFolderAdapter(
    private val context: Context
) :
    BaseAdapter() {

    private var layoutInflater: LayoutInflater? = null
    private lateinit var name: TextView
    private lateinit var count: TextView
    private lateinit var image: ImageView

    var onItemClick: ((FolderItem) -> Unit)? = null

    var courseList: ArrayList<FolderItem> = ArrayList()

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
            convertView = layoutInflater!!.inflate(R.layout.layout_folder_item, null)
        }
        image = convertView!!.findViewById(R.id.image)
        name = convertView.findViewById(R.id.name)
        count = convertView.findViewById(R.id.count)

        if(courseList[position].IsVideo){
            val thumb = ThumbnailUtils.createVideoThumbnail(
                courseList[position].FolderItem.first().FullPath,
                MediaStore.Images.Thumbnails.MINI_KIND
            )
            image.setImageBitmap(thumb)
        }else {
            image.setImageBitmap(BitmapFactory.decodeFile(courseList[position].FolderPhoto))
        }

        name.text = courseList[position].FolderName
        count.text = courseList[position].FolderCount.toString()

        convertView.setOnClickListener {
            onItemClick?.invoke(courseList[position])
        }
        return convertView
    }
}