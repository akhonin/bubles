package com.quick.junk.cleaner.adblock.best.free.app.ui.view.activity

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import com.quick.junk.cleaner.adblock.best.free.app.R
import com.quick.junk.cleaner.adblock.best.free.app.data.FolderItem
import com.quick.junk.cleaner.adblock.best.free.app.data.MediaItem
import com.quick.junk.cleaner.adblock.best.free.app.ui.view.adapter.MediaFolderAdapter
import java.io.File


class CleanerFolderActivity: AppCompatActivity() {

    lateinit var folders: GridView
    var courseAdapter:MediaFolderAdapter? = null

    var intentData:ArrayList<FolderItem>? = null
    var isVideo = false

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

        isVideo = intent.getBooleanExtra("video",false)
        println("CleanerFolderActivity videos  isVideo ${isVideo}")
        if(!isVideo) {
            try {
                intentData = intent.getSerializableExtra("items") as ArrayList<FolderItem>
            }catch (e:Exception){
                e.printStackTrace()
            }
        }else{
            val videos = getVideos()
            videos.forEach {
                println("CleanerFolderActivity videos ${it}")
            }
        }

        println("CleanerFolderActivity ${intentData}")

        folders.adapter = courseAdapter


        findViewById<View>(R.id.icon).setOnClickListener {
            super.onBackPressed()
        }
    }

    fun getVideos():ArrayList<MediaItem> {
        val videoItemHashSet: ArrayList<MediaItem> = ArrayList()
        val projection = arrayOf(
            MediaStore.Video.VideoColumns.DATA,
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT,
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.RELATIVE_PATH,
            MediaStore.Video.Media.DURATION
        )

        val cursor: Cursor? = contentResolver
            .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null)
        try {
            cursor!!.moveToFirst()
            do {
                videoItemHashSet.add(
                    MediaItem(
                        cursor.getString(2),
                        cursor.getString(0),
                        Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, cursor.getString(0).toString()).toString(),
                        cursor.getString(1),
                        cursor.getLong(3),
                        true,
                        cursor.getLong(8)
                ))
            } while (cursor.moveToNext())
            cursor.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return ArrayList(videoItemHashSet)
    }

    override fun onResume() {
        super.onResume()
        showFolder()
    }

    private fun showFolder(){
        courseAdapter!!.courseList.clear()
        if(intentData.isNullOrEmpty()) {
            var photoList = ArrayList<MediaItem>()
            photoList.clear()
            photoList = if(!isVideo) {
                getImages()
            }else{
                getVideos()
            }
            val grouping = HashMap<String, FolderItem>()

            photoList.forEach {
                val file = File(it!!.FullPath)
                if (grouping[file.parentFile.name] == null) {
                    grouping[file.parentFile.name] = FolderItem(file.parentFile.name, 1, file.path,
                        it.IsVideo,
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


    private fun getImages(): ArrayList<MediaItem> {
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

        val arrPath = ArrayList<MediaItem>()

        for (i in 0 until count) {
            cursor.moveToPosition(i)
            val dataColumnIndex: Int = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            val uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cursor.getString(0).toString())
            arrPath.add(MediaItem(
                cursor.getString(1),
                cursor.getString(dataColumnIndex),
                uri.toString(),
                cursor.getString(0),
                cursor.getLong(3),
                false,
                0L
            ))
        }
        cursor.close()
        return arrPath
    }

}