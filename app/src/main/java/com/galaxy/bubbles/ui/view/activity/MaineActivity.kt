package com.galaxy.bubbles.ui.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.galaxy.bubbles.R
import com.galaxy.bubbles.data.Config.Companion.ACTIVITY_CODE_MAIN

class MaineActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maine)

        findViewById<View>(R.id.speed_test).setOnClickListener {
            startActivity(Intent(this, SpeedTestActivity::class.java))
        }

        findViewById<View>(R.id.adblock).setOnClickListener {
            startActivity(Intent(this, AdBlockActivity::class.java))
        }

        findViewById<View>(R.id.premium).setOnClickListener {
            startActivity(Intent(this, PremiumActivity::class.java))
        }
    }

    companion object {
        var lastResumedActivityCode = ACTIVITY_CODE_MAIN
    }
}