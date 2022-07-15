package com.quick.junk.cleaner.adblock.best.free.app.controller

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager


class DeviceController {
    companion object {
        fun getWidthMax(context: Context) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).currentWindowMetrics.bounds.width()
        } else {
            val displayMetrics = DisplayMetrics()
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).getDefaultDisplay().getMetrics(displayMetrics)
             displayMetrics.widthPixels
        }
    }
}