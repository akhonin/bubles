package com.galaxy.bubbles.ui.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.galaxy.bubbles.R

class FirstPrerollActivity: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preroll_first)

        Glide.with(baseContext)
            .load(R.drawable.preroll_anim_1)
            .into(findViewById(R.id.image))

        findViewById<View>(R.id.continue_btn).setOnClickListener {
            startActivity(Intent(this, SecondPrerollActivity::class.java))
            finish()
        }
    }

}