package com.galaxy.bubbles.ui.view.activity

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import com.galaxy.bubbles.R
import com.galaxy.bubbles.data.FolderItem
import com.galaxy.bubbles.data.MediaItem
import com.galaxy.bubbles.ui.view.adapter.MediaFolderAdapter
import java.io.File


class CleanerFolderActivity: AppCompatActivity() {

    lateinit var folders: GridView
    var courseAdapter:MediaFolderAdapter? = null

    var intentData:ArrayList<FolderItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folders)

        folders = findViewById(R.id.idGRV)

        courseAdapter = MediaFolderAdapter(this@CleanerFolderActivity)

        courseAdapter!!.onItemClick ={
            val intent = Intent(this, CleanerGalleryActivity::class.java)
            intent.putParcelableArrayListExtra("items", it.FolderItem)
            startActivity(intent)
        }

        intentData = intent.getSerializableExtra("items") as ArrayList<FolderItem>

        println("CleanerFolderActivity ${intentData}")

        folders.adapter = courseAdapter


        findViewById<View>(R.id.icon).setOnClickListener {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        showFolder()
    }

    private fun showFolder(){
        courseAdapter!!.courseList.clear()
        if(intentData.isNullOrEmpty()) {
            val photoList = getImages()
            val grouping = HashMap<String, FolderItem>()

            photoList.forEach {
                val file = File(it!!.FullPath)
                if (grouping[file.parentFile.name] == null) {
                    grouping[file.parentFile.name] = FolderItem(file.parentFile.name, 1, file.path,
                        arrayListOf(it))
                } else {
                    grouping[file.parentFile.name]!!.FolderCount =
                        grouping[file.parentFile.name]!!.FolderCount + 1
                    grouping[file.parentFile.name]!!.FolderItem.add(it)
                }
            }
            courseAdapter!!.courseList.addAll(grouping.values.toList())
        }else{
            courseAdapter!!.courseList.addAll(intentData!!)
        }
    }


    private fun getImages(): Array<MediaItem?> {
        val columns = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.RELATIVE_PATH
        )
        val orderBy = MediaStore.Images.Media._ID
        val cursor: Cursor? = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
            null, orderBy)
        val count: Int = cursor!!.count

        val arrPath = arrayOfNulls<MediaItem>(count)

        for (i in 0 until count) {
            cursor.moveToPosition(i)
            val dataColumnIndex: Int = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            val uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cursor.getString(0).toString())
            arrPath[i] = MediaItem(
                cursor.getString(1),
                cursor.getString(dataColumnIndex),
                uri.toString(),
                cursor.getString(0)
            )
        }
        cursor.close()
        return arrPath
    }

}