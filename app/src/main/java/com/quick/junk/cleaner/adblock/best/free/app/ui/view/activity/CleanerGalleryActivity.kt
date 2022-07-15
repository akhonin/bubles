package com.quick.junk.cleaner.adblock.best.free.app.ui.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import com.quick.junk.cleaner.adblock.best.free.app.R
import com.quick.junk.cleaner.adblock.best.free.app.data.FolderItem
import com.quick.junk.cleaner.adblock.best.free.app.data.MediaItem
import com.quick.junk.cleaner.adblock.best.free.app.ui.view.adapter.MediaGalleryAdapter


class CleanerGalleryActivity: AppCompatActivity() {

    lateinit var folders: GridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        folders = findViewById(R.id.idGRV)


        val item = intent.getSerializableExtra("items") as ArrayList<MediaItem>

        println("photoList item size ${item.size}")

        val courseAdapter = MediaGalleryAdapter(this@CleanerGalleryActivity)

        courseAdapter.onItemClick ={
            val intent = Intent(this, CleanerFullScreenActivity::class.java)
            intent.putParcelableArrayListExtra("items", item)
            intent.putExtra("photoIndex", it)
            startActivity(intent)
        }

        folders.adapter = courseAdapter
        courseAdapter.courseList = item


        findViewById<View>(R.id.icon).setOnClickListener {
            super.onBackPressed()
        }
    }
}