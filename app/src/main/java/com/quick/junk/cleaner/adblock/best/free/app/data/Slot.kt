package com.quick.junk.cleaner.adblock.best.free.app.data

import com.quick.junk.cleaner.adblock.best.free.app.data.Config.Companion.PATH_CAMERA
import com.quick.junk.cleaner.adblock.best.free.app.data.Config.Companion.PATH_DOWNLOAD
import com.quick.junk.cleaner.adblock.best.free.app.data.Config.Companion.PATH_SCREENSHOTS
import java.util.*

class Slot(var name: String) {
    val directoryPathLinkedList: LinkedList<DirectoryPath> = LinkedList()
    val isVisible = arrayOf(true, true, true) // download, camera, screenshot

    init {
        directoryPathLinkedList.add(DirectoryPath(null, PATH_DOWNLOAD))
        directoryPathLinkedList.add(DirectoryPath(null, PATH_CAMERA))
        directoryPathLinkedList.add(DirectoryPath(null, PATH_SCREENSHOTS))
    }

    class DirectoryPath(val rootUriString: String?, val lastPath: String){
        override fun equals(other: Any?): Boolean {
            if (other == null) return false
            if (javaClass != other.javaClass) return false
            return rootUriString == (other as DirectoryPath).rootUriString
        }

        override fun hashCode(): Int {
            var result = rootUriString?.hashCode() ?: 0
            result = 31 * result + lastPath.hashCode()
            return result
        }
    }
}