package com.galaxy.bubbles.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class FolderItem(
    val FolderName:String? = null,
    var FolderCount:Int = 0,
    val FolderPhoto:String? = null,
    val FolderItem:ArrayList<MediaItem> = ArrayList()
) : Serializable, Parcelable