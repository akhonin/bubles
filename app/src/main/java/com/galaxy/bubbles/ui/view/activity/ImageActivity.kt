package com.galaxy.bubbles.ui.view.activity

import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.forEach
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.galaxy.bubbles.R
import com.galaxy.bubbles.controller.ContentController
import com.galaxy.bubbles.data.Config.Companion.ACTIVITY_CODE_IMAGE
import com.galaxy.bubbles.data.Config.Companion.INTENT_EXTRA_POSITION_CONTENT
import com.galaxy.bubbles.data.Config.Companion.INTENT_EXTRA_POSITION_DIRECTORY
import com.galaxy.bubbles.data.Config.Companion.KEY_DIRECTORY_POSITION
import com.galaxy.bubbles.data.Directory
import com.galaxy.bubbles.ui.view.fragment.ImageFragment
import java.text.SimpleDateFormat
import java.util.*


class ImageActivity : AppCompatActivity() {
    private lateinit var deleteResultLauncher: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var viewPager: ViewPager2
//    private lateinit var contentsPagerAdapter: ImageFragmentStateAdapter

    lateinit var dayOfWeek:TextView
    lateinit var dateText:TextView
    lateinit var photoList: RecyclerView
    lateinit var topView: View
    lateinit var bottomView: View

    private var directoryPosition = 0
    private var isFull = false
    private var isEditFocused = false

    private lateinit var directory: Directory


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        dateText = findViewById(R.id.date)
        dayOfWeek = findViewById(R.id.day_of_week)
        topView = findViewById(R.id.top_item)
        bottomView = findViewById(R.id.bottom_item)
        WindowCompat.setDecorFitsSystemWindows(window, false)


        findViewById<View>(R.id.delete).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val pendingIntent = MediaStore.createDeleteRequest(contentResolver, setOf(getCurrentContent().uri))

                val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent.intentSender).build()
                deleteResultLauncher.launch(intentSenderRequest)
            } else {
                performDeleteImage(getCurrentContent())
            }

        }

        directoryPosition = intent.getIntExtra(INTENT_EXTRA_POSITION_DIRECTORY, 0)
        val contentPosition = intent.getIntExtra(INTENT_EXTRA_POSITION_CONTENT, 0)

        directory = ContentController.directoryArrayList[directoryPosition]

        deleteResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val position = viewPager.currentItem
                directory.contentArrayList.removeAt(position)
//                contentsPagerAdapter.notifyItemRemoved(position)
                if (directory.contentArrayList.size == 0) {
                    ContentController.directoryArrayList.removeAt(directoryPosition)
                    onBackPressed()
                } else {
                    directory.refreshDate()
                    ContentController(this).sortDirectoryArrayList()
                    directoryPosition = ContentController.directoryArrayList.indexOf(directory)
                }
            }
        }


        supportFragmentManager.setFragmentResultListener(KEY_DIRECTORY_POSITION, this) { key: String, bundle: Bundle ->
            if (key == KEY_DIRECTORY_POSITION) {
                directoryPosition = bundle.getInt(KEY_DIRECTORY_POSITION)
            }
        }

        photoList = findViewById(R.id.photo_list)
        val listAdapter = ListRecyclerAdapter().also {
            photoList.forEach {
                it.top = 10
            }
        }
        listAdapter.onItemClick = {
            viewPager.currentItem = it
        }
        photoList.adapter = listAdapter
        photoList.layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.HORIZONTAL, false)
        // initialize view pager
        viewPager = findViewById(R.id.pager_image)
//        contentsPagerAdapter = ImageFragmentStateAdapter(supportFragmentManager, lifecycle)
        viewPager.offscreenPageLimit = 5
//        viewPager.adapter = contentsPagerAdapter
        viewPager.setCurrentItem(contentPosition, false)
        viewPager.requestDisallowInterceptTouchEvent(true)

        viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
            }

            override fun onPageSelected(position: Int) {
                val dateLong = directory.contentArrayList[position].date

                val date = Date(dateLong)
                val format = SimpleDateFormat("dd.MM.yyyy HH:mm")
                val day = SimpleDateFormat("EEEE")
                dateText.text = format.format(date)
                dayOfWeek.text = day.format(date)

                photoList.forEach {
                    it.top = 10
                }

                photoList[position].top = 0
            }
        })
    }



    private val DELETE_PERMISSION_REQUEST = 0x1033

    private fun performDeleteImage(image: Directory.Content) {
            try {
                application.contentResolver.delete(
                    image.uri,
                    "${MediaStore.Images.Media._ID} = ?",
                    arrayOf(image.id.toString())
                )
            } catch (securityException: SecurityException) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val recoverableSecurityException =
                        securityException as? RecoverableSecurityException
                            ?: throw securityException

                    recoverableSecurityException.userAction.actionIntent.intentSender.let {
                        startIntentSenderForResult(
                            recoverableSecurityException.userAction.actionIntent.intentSender,
                            DELETE_PERMISSION_REQUEST,
                            null,
                            0,
                            0,
                            0,
                            null
                        )
                    }
                } else {
                    throw securityException
                }
            }
    }

    override fun onResume() {
        println("onResume ")
        // set last resumed activity code
        MaineActivity.lastResumedActivityCode = ACTIVITY_CODE_IMAGE

        super.onResume()
    }


    override fun onBackPressed() {
        // set result
        intent.putExtra(INTENT_EXTRA_POSITION_DIRECTORY, directoryPosition)
        setResult(RESULT_OK, intent)

        super.onBackPressed()
    }

    fun getCurrentContent(): Directory.Content {
        return directory.contentArrayList[viewPager.currentItem]
    }

    fun fullImage() {
        TransitionManager.beginDelayedTransition(findViewById(R.id.parent))
        topView.isVisible =isFull
        bottomView.isVisible =isFull
        isFull = !isFull
    }

/*    inner class ImageFragmentStateAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun createFragment(position: Int): Fragment {
            println("directoryPosition ${directoryPosition}")
            return ImageFragment.newInstance(directoryPosition, position)
        }

        override fun getItemCount(): Int {
            return directory.contentArrayList.size
        }

        override fun getItemId(position: Int): Long {
            return directory.contentArrayList[position].uri.hashCode().toLong()
        }
    }*/

    inner class ListRecyclerAdapter() : RecyclerView.Adapter<ListRecyclerAdapter.ListViewHolder>() {
        inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val thumbnailImageView: ImageView = itemView.findViewById(R.id.image_thumbnail)
        }

        var onItemClick: ((Int) -> Unit)? = null

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): ListRecyclerAdapter.ListViewHolder {
            return ListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_list, parent, false))
        }

        override fun onBindViewHolder(holder: ListRecyclerAdapter.ListViewHolder, position: Int) {
            holder.itemView.setOnClickListener {
                onItemClick?.invoke(position)
            }
            Glide.with(baseContext)
                .load(directory.contentArrayList[position].uri)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.thumbnailImageView)
        }

        override fun getItemCount() = directory.contentArrayList.size

    }
}