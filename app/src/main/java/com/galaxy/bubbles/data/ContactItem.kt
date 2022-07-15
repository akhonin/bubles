package com.galaxy.bubbles.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class ContactItem(
    val Name:String? = null,
    var Number:String? = null,
    var Count:Int = 0,
    var Id:Long? = null
) : Serializable, Parcelable