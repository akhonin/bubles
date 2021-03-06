package com.quick.junk.cleaner.adblock.best.free.app.data

import android.net.Uri
import com.quick.junk.cleaner.adblock.best.free.app.data.Config.Companion.PATH_PRIMARY

class Directory(val directoryPath: Slot.DirectoryPath) {
    val name = directoryPath.lastPath.substringAfter(PATH_PRIMARY).substringAfterLast("/")
    val contentArrayList: ArrayList<Content> = ArrayList()
    var date = 0L

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (javaClass != other.javaClass) return false

        val otherDirectory = other as Directory
        return directoryPath == otherDirectory.directoryPath
                && date == otherDirectory.date
                && contentArrayList == otherDirectory.contentArrayList
    }

    override fun hashCode(): Int {
        var result = directoryPath.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + contentArrayList.hashCode()
        result = 31 * result + date.hashCode()
        return result
    }

    fun refreshDate() {
        date = contentArrayList.maxOf { it.date }
    }

    class Content(
        val isVideo: Boolean,
        val id: String,

        var name: String,
        val size: String,
        val width: Int,
        val height: Int,
        var date: Long,
        val relativePath: String,

        val uri: Uri,
        val duration: Long
    ) {
        override fun equals(other: Any?): Boolean {
            if (other == null) return false
            if (javaClass != other.javaClass) return false
            return id == (other as Content).id
        }

        override fun hashCode(): Int {
            var result = isVideo.hashCode()
            result = 31 * result + id.hashCode()
            result = 31 * result + name.hashCode()
            result = 31 * result + size.hashCode()
            result = 31 * result + width.hashCode()
            result = 31 * result + height.hashCode()
            result = 31 * result + date.hashCode()
            result = 31 * result + uri.hashCode()
            return result
        }
    }
}