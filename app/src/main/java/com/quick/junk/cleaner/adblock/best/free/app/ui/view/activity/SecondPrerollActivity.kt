package com.quick.junk.cleaner.adblock.best.free.app.ui.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.quick.junk.cleaner.adblock.best.free.app.R

class SecondPrerollActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preroll_second)

        Glide.with(baseContext)
            .load(R.drawable.preroll_anim_2)
            .into(findViewById(R.id.image))

        findViewById<View>(R.id.continue_btn).setOnClickListener {
            startActivity(Intent(this, ThirdPrerollActivity::class.java))
            finish()
        }
    }


}