package com.quick.junk.cleaner.adblock.best.free.app.data

import android.Manifest


class Config {
    companion object {
        // permission
        val PERMISSION_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        // request code
        const val REQUEST_CODE_PERMISSION = 1000

        // dialog
        const val DIALOG_TYPE_PERMISSION = 0
        const val DIALOG_TYPE_SORT_DIRECTORY = 10
        const val DIALOG_TYPE_SORT_CONTENT = 11
        const val DIALOG_TYPE_INFORMATION = 12
        const val DIALOG_WIDTH_PERCENTAGE_DEFAULT = 0.85
        const val DIALOG_WIDTH_PERCENTAGE_RECYCLER = 0.65

        // activity
        const val INTENT_EXTRA_POSITION_DIRECTORY = "position_directory"
        const val INTENT_EXTRA_POSITION_CONTENT = "position_content"
        const val INTENT_EXTRA_NAME = "name"
        const val INTENT_EXTRA_URI = "uri"

        // fragment
        const val KEY_STACK = "stack"
        const val KEY_DIRECTORY_POSITION = "position_directory"
        const val REQUEST_KEY_COPY = "copy"
        const val ACTIVITY_CODE_MAIN = 0
        const val ACTIVITY_CODE_SETTING = 1
        const val ACTIVITY_CODE_IMAGE = 2
        const val ACTIVITY_CODE_VIDEO = 3
        const val ACTIVITY_CODE_CHOICE = 4

        // recycler view
        const val SPAN_COUNT_DIRECTORY = 2
        const val SPAN_COUNT_CONTENT = 3
        const val INFORMATION_POSITION_NAME = 0
        const val INFORMATION_POSITION_DATE = 1
        const val INFORMATION_POSITION_TIME = 2
        const val INFORMATION_POSITION_SIZE = 3
        const val INFORMATION_POSITION_WIDTH = 4
        const val INFORMATION_POSITION_HEIGHT = 5
        const val INFORMATION_POSITION_PATH = 6

        // setting
        const val SETTING_POSITION_SLOT = 0
        const val SETTING_POSITION_DIRECTORY = 1
        const val PATH_PRIMARY = "primary:"
        const val PATH_DOWNLOAD = "Download"
        const val PATH_CAMERA = "DCIM/Camera"
        const val PATH_SCREENSHOTS = "Pictures/Screenshots"
        const val COUNT_DEFAULT_DIRECTORY = 3

        // prefs
        const val PREFS = "prefs"
        const val KEY_SLOT_LIST = "slot_list"
        const val KEY_SELECTED_SLOT_POSITION = "selected_slot"
        const val KEY_DIRECTORY_SORT_ORDER = "directory_sort_order"
        const val KEY_CONTENT_SORT_ORDER = "content_sort_order"
        const val SORT_POSITION_BY_NAME = 0
        const val SORT_POSITION_BY_NEWEST = 1
        const val SORT_POSITION_BY_OLDEST = 2

        // others
        const val MIME_TYPE_IMAGE = "image/*"
        const val MIME_TYPE_VIDEO = "video/*"
        const val MIME_TYPE_ALL = "*/*"
        const val FORMAT_DATE = "yyyy-MM-dd"
        const val FORMAT_TIME = "HH:mm"
        const val NAME_DUMMY = "empty.png"

        const val IMAGES_DIPLICATES_KEY = "IMAGES_DIPLICATES_KEY"
        const val CONTACTS_DIPLICATES_KEY = "CONTACTS_DIPLICATES_KEY"
        const val IS_FUNNEL = "is_funnel"
        const val IS_FIRST_RUN_WITH_PAYWALL_KEY = "FIRST_WITH_PAYWALL_KEY"
        const val IS_FIRST_RUN_WITH_SCAN_KEY = "FIRST_WITH_SCAN_KEY"
        const val IS_FIRST_RUN_WITH_ALERT_KEY = "FIRST_WITH_ALERT_KEY"
        const val IS_FIRST_RUN_KEY = "FIRST_KEY"
        const val WHITE_ENABLED = "WHITE_ENABLED"
        const val IS_NO_VIP_PREF_KEY = "NO_VIP_PREF"
    }
}