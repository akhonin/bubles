package com.galaxy.bubbles.ui.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.galaxy.bubbles.R
import com.galaxy.bubbles.data.Config
import com.galaxy.bubbles.data.ContactItem
import com.galaxy.bubbles.data.FolderItem
import com.galaxy.bubbles.data.MediaItem
import com.galaxy.bubbles.interfaces.UpdateClinerInfo
import com.galaxy.bubbles.service.ForegroundService
import com.galaxy.bubbles.util.SharedSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CleanerActivity: AppCompatActivity() {

    lateinit var contactsSize:TextView
    lateinit var imagesSize:TextView
    lateinit var imagesDoubleSize:TextView
    lateinit var contactsDoubleSize:TextView
    lateinit var screenSize:TextView

    var duplicateImagesArr = arrayListOf<FolderItem>()
    var duplicateContactArr = arrayListOf<ContactItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cleaner)

        contactsSize = findViewById(R.id.contacts_count)
        imagesSize = findViewById(R.id.files_count)
        imagesDoubleSize = findViewById(R.id.photo_dubles_count)
        contactsDoubleSize = findViewById(R.id.contacts_double_count)
        screenSize = findViewById(R.id.screens_double_count)

        findViewById<View>(R.id.all_photos).setOnClickListener {
            startActivity(Intent(this, CleanerFolderActivity::class.java))
        }

        findViewById<View>(R.id.dublicates_photo).setOnClickListener {
            val intent = Intent(this, CleanerFolderActivity::class.java)
            intent.putParcelableArrayListExtra("items", duplicateImagesArr)
            startActivity(intent)
        }
        findViewById<View>(R.id.dublicates_contacts).setOnClickListener {
            val intent = Intent(this, ContactListActivity::class.java)
            intent.putParcelableArrayListExtra("contacts", duplicateContactArr)
            startActivity(intent)
        }


        getInfoCount()
        getDuplesInfo()

        iUpdate = object : UpdateClinerInfo {
            override fun update() {
                getDuplesInfo()
            }
        }
    }


    fun getInfoCount(){
        val requestContactsPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    ForegroundService.startService(this, "Foreground Service is running...")
                    contactsSize.text = "${getContacts()} contacts"

                    val images = getImages()
                    val imagesDupSize = images.filter { !it!!.FullPath.contains("Screenshots") }.size
                    val screenDupSize = images.filter { it!!.FullPath.contains("Screenshots") }.size
                    imagesSize.text = "${imagesDupSize} files"
                    screenSize.text = "${screenDupSize} files"
                } else { // Do something as the permission is not granted
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }
            }


        val requestWriteContactPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    requestContactsPermission.launch(Manifest.permission.READ_CONTACTS)
                } else { // Do something as the permission is not granted
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }
            }
        val requestPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    requestWriteContactPermission.launch(Manifest.permission.WRITE_CONTACTS)
                } else { // Do something as the permission is not granted
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }
            }

        requestPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    @SuppressLint("Range")
    fun getContacts():Int{
        val contacts = ArrayList<ContactItem>()
        val phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC")
        while (phones!!.moveToNext()) {
            val name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            val id = phones.getLong(phones.getColumnIndex(ContactsContract.RawContacts._ID))

            contacts.add(ContactItem(name,phoneNumber,1, id))
        }
        phones.close()

        return contacts.size
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


    fun getDuplesInfo(){
        val imagesString = SharedSettings.getString(Config.IMAGES_DIPLICATES_KEY)
        val contactString = SharedSettings.getString(Config.CONTACTS_DIPLICATES_KEY)
        if(!imagesString.isNullOrEmpty()){
            try {
                val itemType = object : TypeToken<List<FolderItem>>() {}.type
                duplicateImagesArr.clear()
                duplicateImagesArr.addAll(Gson().fromJson<List<FolderItem>>(imagesString, itemType))
                var count = 0
                duplicateImagesArr.forEach {
                    count += it.FolderCount
                }
                imagesDoubleSize.text = "${count} duplicates"
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        if(!contactString.isNullOrEmpty()){
            try {
                val itemType = object : TypeToken<List<ContactItem>>() {}.type
                duplicateContactArr.clear()
                duplicateContactArr.addAll(Gson().fromJson<List<ContactItem>>(contactString, itemType))
                contactsDoubleSize.text = "${duplicateContactArr.size} duplicates"
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        println("getDuplesInfo ${imagesString}")
    }

    companion object {
        var iUpdate: UpdateClinerInfo? = null

        fun showDuplicates(){
            println("showDuplicates")
            iUpdate?.update()
        }
    }

/*    fun getImagesPath(): ArrayList<String?> {
        val listOfAllImages = ArrayList<String?>()
        val cursor: Cursor?
        val column_index_data: Int
        val column_index_folder_name: Int
        var PathOfImage: String? = null
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaColumns.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        cursor = contentResolver.query(uri, projection, null,
            null, null)
        column_index_data = cursor!!.getColumnIndexOrThrow(MediaColumns.DATA)
        column_index_folder_name = cursor!!
            .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data)
            listOfAllImages.add(PathOfImage)
        }
        return listOfAllImages
    }

    companion object {
        fun showDuplicates(){
            println("showDuplicates")
        }
    }*/
}