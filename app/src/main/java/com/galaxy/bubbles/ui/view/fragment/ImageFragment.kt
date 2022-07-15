package com.galaxy.bubbles.ui.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.galaxy.bubbles.R
import com.galaxy.bubbles.controller.ContentController
import com.galaxy.bubbles.data.Config.Companion.INTENT_EXTRA_NAME
import com.galaxy.bubbles.data.Config.Companion.INTENT_EXTRA_URI
import com.galaxy.bubbles.data.MediaItem
import com.galaxy.bubbles.ui.view.activity.CleanerFullScreenActivity
import com.galaxy.bubbles.ui.view.activity.ImageActivity
import com.galaxy.bubbles.ui.view.activity.VideoActivity
import com.github.chrisbanes.photoview.PhotoView
import java.util.concurrent.TimeUnit

class ImageFragment(images: ArrayList<MediaItem>, contentPosition: Int) : Fragment() {
    companion object {
        fun newInstance(images: ArrayList<MediaItem>, contentPosition: Int): ImageFragment {
            return ImageFragment(images, contentPosition)
        }
    }

    private lateinit var rootView: View
    private lateinit var photoView: PhotoView
    private lateinit var playImageView: ImageView
    private lateinit var timeTextView: TextView

    private val content = images[contentPosition]

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // initialize root view
        rootView = inflater.inflate(R.layout.fragment_image, container, false)

        // initialize views
        photoView = rootView.findViewById(R.id.photo_image)
        photoView.setOnClickListener { (activity as CleanerFullScreenActivity).fullImage() }
        playImageView = rootView.findViewById(R.id.play_image)
        timeTextView = rootView.findViewById(R.id.time_image)

        // load image
        Glide.with(requireContext())
            .load(content.FullPath)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(photoView)

/*        // case video
        if (content.isVideo) {
            playImageView.visibility = View.VISIBLE
            timeTextView.visibility = View.VISIBLE

            val minutes = TimeUnit.MILLISECONDS.toMinutes(content.duration)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(content.duration) % 60
            val time = String.format("%02d:%02d", minutes, seconds)
            timeTextView.text = time

            photoView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black))
            photoView.isZoomable = false

            playImageView.setOnClickListener {
                val intent = Intent(context, VideoActivity::class.java)
                intent.putExtra(INTENT_EXTRA_NAME, content.name)
                intent.putExtra(INTENT_EXTRA_URI, content.uri.toString())
                startActivity(intent)
            }
        }*/

        return rootView
    }
}