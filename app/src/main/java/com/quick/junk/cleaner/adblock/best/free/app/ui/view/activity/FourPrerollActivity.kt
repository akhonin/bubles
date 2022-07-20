package com.quick.junk.cleaner.adblock.best.free.app.ui.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.quick.junk.cleaner.adblock.best.free.app.R

class FourPrerollActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preroll_four)

        Glide.with(baseContext)
            .load(R.drawable.preroll_anim_4)
            .into(findViewById(R.id.image))

        findViewById<View>(R.id.continue_btn).setOnClickListener {
            startActivity(Intent(this, FirstSubActivity::class.java))
            finish()
        }
    }
}