package com.galaxy.bubbles.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.galaxy.bubbles.R
import com.galaxy.bubbles.data.Config.Companion.CONTACTS_DIPLICATES_KEY
import com.galaxy.bubbles.data.Config.Companion.IMAGES_DIPLICATES_KEY
import com.galaxy.bubbles.data.ContactItem
import com.galaxy.bubbles.data.FolderItem
import com.galaxy.bubbles.data.MediaItem
import com.galaxy.bubbles.ui.view.activity.CleanerActivity
import com.galaxy.bubbles.util.SharedSettings
import com.google.gson.Gson
import java.io.File

class ForegroundService : Service() {
    private val CHANNEL_ID = "ForegroundService Kotlin"
    companion object {
        fun startService(context: Context, message: String) {
            val startIntent = Intent(context, ForegroundService::class.java)
            startIntent.putExtra("inputExtra", message)
            ContextCompat.startForegroundService(context, startIntent)
        }
        fun stopService(context: Context) {
            val stopIntent = Intent(context, ForegroundService::class.java)
            context.stopService(stopIntent)
        }
    }

    private var screenShootScanned = false
    private var imagesScanned = false
    private var contactsScanned = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //do heavy work on a background thread
        val input = intent?.getStringExtra("inputExtra")
        createNotificationChannel()
        val notificationIntent = Intent(this, CleanerActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Searching duplicates")
            .setContentText(input)
            .setProgress(0,0,true)
            .setSmallIcon(R.drawable.ic_cleaner)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
        //stopSelf();

        searchImageDuplicate()
        getContacts()

        return START_NOT_STICKY
    }
    override fun onBind(intent: Intent): IBinder? {
        return null
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }

    fun searchImageDuplicate(){
        val grouping = HashMap<String, FolderItem>()

        val images = getImages()
        if(!images.isNullOrEmpty()) {
            val folders = images.filter { !it!!.FullPath.contains("Screenshots") }

            folders.forEachIndexed { ix, first ->
                val file = File(first!!.FullPath)
                folders.forEachIndexed { iy, second ->
                    if(iy!=ix&&isEqual(first.FullPath,second!!.FullPath)){
                        if(isEqual(first.FullPath,second.FullPath)) {
                            if (grouping[file.parentFile.name] == null) {
                                grouping[file.parentFile.name] =
                                    FolderItem(file.parentFile.name, 1, file.path,
                                        arrayListOf(first))
                            } else {
                                grouping[file.parentFile.name]!!.FolderCount =
                                    grouping[file.parentFile.name]!!.FolderCount + 1
                                grouping[file.parentFile.name]!!.FolderItem.add(first)
                            }
                        }
                    }

                }
            }

            SharedSettings.setString(IMAGES_DIPLICATES_KEY, Gson().toJson(grouping.values))

            imagesScanned = true
            checkScanned()
        }


    }

    fun checkScanned(){
        if(imagesScanned&&contactsScanned){
            try {
                stopService(baseContext)
                CleanerActivity.showDuplicates()
            }catch (e:Exception){
                e.printStackTrace()
            }
            imagesScanned = false
            contactsScanned = false
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

    private fun isEqual(firstFile: String, secondFile: String): Boolean {
        if (File(firstFile).length() != File(secondFile).length()) {
            return false
        }
        val firstStream = File(firstFile).bufferedReader().readLines().toString()
        val secondStream = File(firstFile).bufferedReader().readLines().toString()

        return firstStream.contains(secondStream)
    }

    @SuppressLint("Range")
    fun getContacts(){
        val contacts = ArrayList<ContactItem>()
        val dupl = ArrayList<ContactItem>()
        val phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC")
        while (phones!!.moveToNext()) {
            val name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            val id = phones.getLong(phones.getColumnIndex(ContactsContract.RawContacts._ID))

            contacts.add(ContactItem(name,phoneNumber,1, id))
        }
        phones.close()

        contacts.forEach { first->
            contacts.forEach { second->
                if(first.Id!=second.Id&&first.Number==second.Number){
                    dupl.add(second)
                }
            }
        }

        SharedSettings.setString(CONTACTS_DIPLICATES_KEY,Gson().toJson(dupl))

        contactsScanned = true
        checkScanned()
    }
}