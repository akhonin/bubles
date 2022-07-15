package com.quick.junk.cleaner.adblock.best.free.app.util

import android.content.Context
import com.quick.junk.cleaner.adblock.best.free.app.App
import com.quick.junk.cleaner.adblock.best.free.app.BuildConfig


class SharedSettings {

    companion object {

        private const val APPLICATION_NAME = BuildConfig.APPLICATION_ID

        fun setString(key: String, value: String?) {
            val editor = App.applicationContext().getSharedPreferences(
                APPLICATION_NAME, Context.MODE_PRIVATE).edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun getString(key: String): String {
            val settings = App.applicationContext().getSharedPreferences(
                APPLICATION_NAME, Context.MODE_PRIVATE)
            return settings.getString(key, "") ?: ""
        }

        fun setInt(key: String, value: Int) {
            val editor = App.applicationContext().getSharedPreferences(
                APPLICATION_NAME, Context.MODE_PRIVATE).edit()
            editor.putInt(key, value)
            editor.apply()
        }

        fun getInt(key: String): Int {
            val settings = App.applicationContext().getSharedPreferences(
                APPLICATION_NAME, Context.MODE_PRIVATE)
            return settings.getInt(key, 0)
        }

        fun setBoolean(key: String, value: Boolean) {
            val editor = App.applicationContext().getSharedPreferences(
                APPLICATION_NAME, Context.MODE_PRIVATE).edit()
            editor.putBoolean(key, value)
            editor.apply()
        }

        fun getBoolean(key: String): Boolean {
            val settings = App.applicationContext().getSharedPreferences(
                APPLICATION_NAME, Context.MODE_PRIVATE)
            return settings.getBoolean(key, true)
        }

        fun getTryBoolean(key: String): Boolean {
            val settings = App.applicationContext().getSharedPreferences(
                APPLICATION_NAME, Context.MODE_PRIVATE)
            return settings.getBoolean(key, false)
        }
    }
}