package com.galaxy.bubbles.ui.view.activity

import android.app.Activity
import android.app.RecoverableSecurityException
import android.net.Uri
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
import com.galaxy.bubbles.data.Directory
import com.galaxy.bubbles.data.FolderItem
import com.galaxy.bubbles.data.MediaItem
import com.galaxy.bubbles.ui.view.fragment.ImageFragment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CleanerFullScreenActivity: AppCompatActivity() {
    private lateinit var deleteResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    private lateinit var viewPager: ViewPager2
    private lateinit var contentsPagerAdapter: ImageFragmentStateAdapter
    private var images = ArrayList<MediaItem>()
    private var isFull = false

    lateinit var dayOfWeek: TextView
    lateinit var dateText: TextView
    lateinit var photoList: RecyclerView
    lateinit var topView: View
    lateinit var bottomView: View

    var curentItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        images = intent.getSerializableExtra("items") as ArrayList<MediaItem>
        val selectedItem = intent.getIntExtra("photoIndex",0)

        dateText = findViewById(R.id.date)
        dayOfWeek = findViewById(R.id.day_of_week)
        topView = findViewById(R.id.top_item)
        bottomView = findViewById(R.id.bottom_item)

        viewPager = findViewById(R.id.pager_image)
        contentsPagerAdapter = ImageFragmentStateAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = contentsPagerAdapter
        viewPager.setCurrentItem(selectedItem, false)
        viewPager.requestDisallowInterceptTouchEvent(true)

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
                curentItem = position
                val dateLong = File(images[position].FullPath).lastModified()

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


        deleteResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val position = viewPager.currentItem
                images.removeAt(position)
//                contentsPagerAdapter.notifyItemRemoved(position)
                if (images.size == 0) {
                    onBackPressed()
                }
            }
        }


        findViewById<View>(R.id.delete).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val pendingIntent = MediaStore.createDeleteRequest(contentResolver, setOf(Uri.parse(images[curentItem].FileUri)))

                val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent.intentSender).build()
                deleteResultLauncher.launch(intentSenderRequest)
            } else {
                performDeleteImage(images[curentItem])
            }

        }
    }

    private fun performDeleteImage(image: MediaItem) {
        try {
            application.contentResolver.delete(
                Uri.parse(image.FileUri),
                "${MediaStore.Images.Media._ID} = ?",
                arrayOf(image.Id)
            )
        } catch (securityException: SecurityException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val recoverableSecurityException =
                    securityException as? RecoverableSecurityException
                        ?: throw securityException

                recoverableSecurityException.userAction.actionIntent.intentSender.let {
                    startIntentSenderForResult(
                        recoverableSecurityException.userAction.actionIntent.intentSender,
                        0x1033,
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

    fun fullImage() {
        TransitionManager.beginDelayedTransition(findViewById(R.id.parent))
        topView.isVisible =isFull
        bottomView.isVisible =isFull
        isFull = !isFull
    }

    inner class ImageFragmentStateAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
        : FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun createFragment(position: Int): Fragment {
            return ImageFragment.newInstance(images, position)
        }

        override fun getItemCount(): Int {
            return images.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
    }

    inner class ListRecyclerAdapter : RecyclerView.Adapter<ListRecyclerAdapter.ListViewHolder>() {
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
                .load(images[position])
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.thumbnailImageView)
        }

        override fun getItemCount() = images.size

    }
}