package com.quick.junk.cleaner.adblock.best.free.app.ui.view.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.quick.junk.cleaner.adblock.best.free.app.R
import com.quick.junk.cleaner.adblock.best.free.app.controller.ContentController
import com.quick.junk.cleaner.adblock.best.free.app.controller.DeviceController
import com.quick.junk.cleaner.adblock.best.free.app.controller.SlotController
import com.quick.junk.cleaner.adblock.best.free.app.data.Config.Companion.INTENT_EXTRA_POSITION_DIRECTORY
import com.quick.junk.cleaner.adblock.best.free.app.data.Config.Companion.KEY_DIRECTORY_SORT_ORDER
import com.quick.junk.cleaner.adblock.best.free.app.data.Config.Companion.KEY_STACK
import com.quick.junk.cleaner.adblock.best.free.app.data.Config.Companion.MIME_TYPE_ALL
import com.quick.junk.cleaner.adblock.best.free.app.data.Config.Companion.MIME_TYPE_IMAGE
import com.quick.junk.cleaner.adblock.best.free.app.data.Config.Companion.MIME_TYPE_VIDEO
import com.quick.junk.cleaner.adblock.best.free.app.data.Config.Companion.REQUEST_KEY_COPY
import com.quick.junk.cleaner.adblock.best.free.app.data.Config.Companion.SPAN_COUNT_DIRECTORY
import com.quick.junk.cleaner.adblock.best.free.app.data.Directory
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener

class DirectoryFragment: Fragment() {
    companion object {
        fun newInstance(): DirectoryFragment {
            return DirectoryFragment()
        }
    }

    private lateinit var rootView: View
    private lateinit var copyResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var deleteResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    private lateinit var slotController: SlotController
    private lateinit var contentController: ContentController
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: DirectoryRecyclerAdapter
    private lateinit var dragSelectTouchListener: DragSelectTouchListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // initialize root view
        rootView = inflater.inflate(R.layout.fragment_main, container, false)

        // set options menu
        setHasOptionsMenu(true)

        deleteResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                // initialize selected array
                val selectedArray = recyclerAdapter.selectedHashSet.toIntArray()
                selectedArray.sortDescending()

                // remove directory
                for (position in selectedArray) {
                    ContentController.directoryArrayList.removeAt(position)
                    recyclerAdapter.notifyItemRemoved(position)
                }

                // clear selected hash set
                recyclerAdapter.selectedHashSet.clear()

                // set is selecting false
                recyclerAdapter.isSelecting = false

                // refresh action bar menu
                (context as FragmentActivity).invalidateOptionsMenu()
            }
        }

        (context as FragmentActivity).supportFragmentManager.setFragmentResultListener(KEY_DIRECTORY_SORT_ORDER, viewLifecycleOwner) { key: String, _: Bundle ->
            if (key == KEY_DIRECTORY_SORT_ORDER) {
                contentController.sortDirectoryArrayList()
                recyclerAdapter.notifyItemRangeChanged(0, ContentController.directoryArrayList.size, false)
            }
        }
        (context as FragmentActivity).supportFragmentManager.setFragmentResultListener(REQUEST_KEY_COPY, viewLifecycleOwner) { _: String, _: Bundle ->
            val selectedDirectoryHashSet = HashSet<Directory>()
            for (selectedDirectoryPosition in recyclerAdapter.selectedHashSet)
                selectedDirectoryHashSet.add(ContentController.directoryArrayList[selectedDirectoryPosition])
            contentController.sortDirectoryArrayList()
            recyclerAdapter.selectedHashSet.clear()
            for (selectedDirectory in selectedDirectoryHashSet)
                recyclerAdapter.selectedHashSet.add(ContentController.directoryArrayList.indexOf(selectedDirectory))

            recyclerAdapter.notifyItemRangeChanged(0, ContentController.directoryArrayList.size, false)
        }

        slotController = SlotController(requireContext())

        contentController = ContentController(requireContext())

        recyclerView = rootView.findViewById(R.id.recycler_thumbnail)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context, SPAN_COUNT_DIRECTORY)

        recyclerAdapter = DirectoryRecyclerAdapter()
        recyclerAdapter.setHasStableIds(true)
        recyclerAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        recyclerView.adapter = recyclerAdapter

        val onDragSelectionListener =
            DragSelectTouchListener.OnDragSelectListener { start: Int, end: Int, isSelected: Boolean ->
                recyclerAdapter.setRangeSelected(start, end, isSelected)
            }

        dragSelectTouchListener = DragSelectTouchListener()
            .withSelectListener(onDragSelectionListener)
            .withMaxScrollDistance(24)

        if (ContentController.directoryArrayList.size == 0) {
            refresh()
        }

        return rootView
    }

    fun refresh() {
        Handler(Looper.myLooper()!!).post {
            // check is content changed
            val selectedSlot = slotController.getSelectedSlot()
            val messageTextView = rootView.findViewById<TextView>(R.id.message_main)

            // case no slot
            if (selectedSlot == null) {
                messageTextView.setText(R.string.message_error_no_slot)
                messageTextView.visibility = View.VISIBLE
                recyclerView.visibility = View.INVISIBLE
            } else {
                messageTextView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                // backup directory array list
                val backupDirectoryArrayList: ArrayList<Directory> = ArrayList()
                for (directory in ContentController.directoryArrayList) backupDirectoryArrayList.add(directory)

                // initialize contents
                contentController.initializeContents()

                // case content changed
                if (ContentController.directoryArrayList != backupDirectoryArrayList) {
                    // case launch application
                    if (backupDirectoryArrayList.size == 0)
                        notifyDataSetChanged()
                    else {
                        recyclerAdapter.selectedHashSet.clear()
                        recyclerAdapter.isSelecting = false
                        (context as FragmentActivity).invalidateOptionsMenu()
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyDataSetChanged() {
        recyclerAdapter.notifyDataSetChanged()
    }

    fun onBackPressed(): Boolean {
        if (recyclerAdapter.isSelecting) {
            recyclerAdapter.setSelectedAll(false)
            recyclerAdapter.isSelecting = false
            (context as FragmentActivity).invalidateOptionsMenu()
            return true
        }
        return false
    }

    inner class DirectoryRecyclerAdapter : RecyclerView.Adapter<DirectoryRecyclerAdapter.DirectoryViewHolder>() {
        private val screenWidth = DeviceController.getWidthMax(requireContext())
        val selectedHashSet: HashSet<Int> = HashSet()
        var isSelecting = false

        inner class DirectoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val thumbnailImageView: ImageView = itemView.findViewById(R.id.image_thumbnail)
            val selectImageView: ImageView = itemView.findViewById(R.id.select_thumbnail)
            val titleTextView: TextView = itemView.findViewById(R.id.title_thumbnail)
            val contentTextView: TextView = itemView.findViewById(R.id.content_thumbnail)

            init {
                itemView.setOnClickListener {
                    // initialize position
                    val position = it.verticalScrollbarPosition
                    if (position == RecyclerView.NO_POSITION)
                        return@setOnClickListener

                    if (isSelecting) {
                        // toggle selected
                        toggleSelected(position)

                        // case undo
                        if (selectedHashSet.size == 0) {
                            // set is selecting
                            isSelecting = false

                            // refresh action bar menu
                            (context as FragmentActivity).invalidateOptionsMenu()
                        }
                    } else {
                        // replace fragment
          /*              activity!!.supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.idfragment_main, ContentFragment.newInstance(position))
                            .addToBackStack(KEY_STACK)
                            .commit()*/
                    }
                }
/*                itemView.setOnLongClickListener { view: View ->
                    // initialize position
                    val position = bindingAdapterPosition
                    if (position == RecyclerView.NO_POSITION)
                        return@setOnLongClickListener false

                    // perform haptic feedback
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

                    // set is selecting
                    isSelecting = true

                    // set selected
                    setSelected(position, true)

                    // refresh action bar menu
                    (context as FragmentActivity).invalidateOptionsMenu()

                    // start drag
                    dragSelectTouchListener.startDragSelection(position)

                    true
                }*/
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectoryViewHolder {
            return DirectoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_thumbnail, parent, false))
        }

        override fun onBindViewHolder(holder: DirectoryViewHolder, position: Int) {
            val directory = ContentController.directoryArrayList[position]
            val content = directory.contentArrayList[0]

            // case thumbnail
            Glide.with(context!!)
                .load(content.uri)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .override(screenWidth / SPAN_COUNT_DIRECTORY)
                .into(holder.thumbnailImageView)

            // case select
            holder.selectImageView.visibility = if (selectedHashSet.contains(position)) View.VISIBLE else View.GONE

            // case title
            holder.titleTextView.text = directory.name

            // case content
            holder.contentTextView.text = directory.contentArrayList.size.toString()
        }

        override fun getItemCount(): Int {
            return ContentController.directoryArrayList.size
        }

        override fun getItemId(position: Int): Long {
            return ContentController.directoryArrayList[position].directoryPath.hashCode().toLong()
        }

        fun setSelected(position: Int, isSelected: Boolean) {
            if (isSelected) selectedHashSet.add(position)
            else selectedHashSet.remove(position)
            notifyItemChanged(position, false)
            activity!!.title = selectedHashSet.size.toString() + "/" + itemCount
        }

        fun setRangeSelected(startPosition: Int, endPosition: Int, isSelected: Boolean) {
            for (position in startPosition..endPosition) {
                if (isSelected) selectedHashSet.add(position)
                else selectedHashSet.remove(position)
                notifyItemChanged(position, false)
            }
            activity!!.title = selectedHashSet.size.toString() + "/" + itemCount
        }

        fun setSelectedAll(isSelected: Boolean) {
            if (isSelected) {
                for (i in 0 until itemCount) selectedHashSet.add(i)
            } else
                selectedHashSet.clear()
            notifyItemRangeChanged(0, itemCount, false)
            activity!!.title = selectedHashSet.size.toString() + "/" + itemCount
        }

        fun toggleSelected(position: Int) {
            if (selectedHashSet.contains(position)) selectedHashSet.remove(position)
            else selectedHashSet.add(position)
            notifyItemChanged(position, false)
            activity!!.title = selectedHashSet.size.toString() + "/" + itemCount
        }

        fun share() {
            // initialize selected array
            val selectedArray = selectedHashSet.toIntArray()

            // initialize content uri array list
            val contentUriLinkedList: ArrayList<Uri> = ArrayList()
            var isContainVideo = false
            var isContainImage = false
            for (position in selectedArray) {
                // initialize directory
                val directory = ContentController.directoryArrayList[position]

                // add directory contents
                for (content in directory.contentArrayList) {
                    // set is contain video, image
                    if (content.isVideo) isContainVideo = true
                    else isContainImage = true

                    // add content uri
                    contentUriLinkedList.add(content.uri)
                }
            }

            // initialize share intent
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND_MULTIPLE
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, contentUriLinkedList)
                type = if (isContainVideo && isContainImage) MIME_TYPE_ALL else if (isContainVideo) MIME_TYPE_VIDEO else MIME_TYPE_IMAGE
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.action_share)))
        }

        fun delete() {
            // initialize selected array
            val selectedArray = selectedHashSet.toIntArray()

            // initialize content uri array list
            val contentUriLinkedList: ArrayList<Uri> = ArrayList()
            for (position in selectedArray) {
                // initialize directory
                val directory = ContentController.directoryArrayList[position]

                // add content uris
                for (content in directory.contentArrayList) contentUriLinkedList.add(content.uri)
            }

            // initialize create delete request pending intent
            val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                MediaStore.createDeleteRequest(requireContext().contentResolver, contentUriLinkedList)
            } else {
                TODO("VERSION.SDK_INT < R")
            }
            val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent.intentSender).build()

            // launch intent sender request
            deleteResultLauncher.launch(intentSenderRequest)
        }
    }
}