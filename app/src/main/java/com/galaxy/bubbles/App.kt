package com.galaxy.bubbles

import android.content.Context
import androidx.multidex.MultiDexApplication

class App: MultiDexApplication() {

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        const val CERTIFICATES = 1
        var instance: App? = null
        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }
}