package com.quick.junk.cleaner.adblock.best.free.app.ui.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import com.quick.junk.cleaner.adblock.best.free.app.R
import com.quick.junk.cleaner.adblock.best.free.app.data.Config
import com.quick.junk.cleaner.adblock.best.free.app.data.FolderItem
import com.quick.junk.cleaner.adblock.best.free.app.data.MediaItem
import com.quick.junk.cleaner.adblock.best.free.app.ui.view.adapter.MediaGalleryAdapter
import java.io.File


class CleanerGalleryActivity: AppCompatActivity() {

    lateinit var folders: GridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        folders = findViewById(R.id.idGRV)

        val item = intent.getSerializableExtra("items") as ArrayList<MediaItem>

        val courseAdapter = MediaGalleryAdapter(this@CleanerGalleryActivity)

        courseAdapter.onItemClick ={
            if(!item[it].IsVideo) {
                val intent = Intent(this, CleanerFullScreenActivity::class.java)
                intent.putParcelableArrayListExtra("items", item)
                intent.putExtra("photoIndex", it)
                startActivity(intent)
            }else{
                val intent = Intent(this, VideoActivity::class.java)
                println("item FileUri ${item[it].FileUri}")
                println("item FullPath ${item[it].FullPath}")
                intent.putExtra(Config.INTENT_EXTRA_URI, item[it].FullPath)
                intent.putExtra(Config.INTENT_EXTRA_NAME, item[it].Name)
//                intent.putExtra("photoIndex", it)
                startActivity(intent)
            }
        }

        folders.adapter = courseAdapter
        courseAdapter.courseList = item


        findViewById<View>(R.id.icon).setOnClickListener {
            super.onBackPressed()
        }
    }
}