package com.quick.junk.cleaner.adblock.best.free.app.ui.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.quick.junk.cleaner.adblock.best.free.app.R

class GreyFirstActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grey_first)

        Glide.with(baseContext)
            .load(R.drawable.grey_1)
            .into(findViewById(R.id.image))

        Glide.with(baseContext)
            .load(R.drawable.grey_first)
            .into(findViewById(R.id.animation))
    }

}