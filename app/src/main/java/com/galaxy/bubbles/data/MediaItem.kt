package com.galaxy.bubbles.data

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class MediaItem(
    val Name:String,
    val FullPath:String,
    var FileUri: String,
    val Id: String,
    var Size:Long
) : Serializable, Parcelable