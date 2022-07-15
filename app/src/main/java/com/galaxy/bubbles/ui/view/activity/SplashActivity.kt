package com.galaxy.bubbles.ui.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.amplitude.api.Amplitude
import com.bumptech.glide.Glide
import com.galaxy.bubbles.R

class SplashActivity: AppCompatActivity() {
    lateinit var loadProgress: TextView

    private var funnel:String? = null

    companion object {
        var isInitFacebook = false
        var isInitFirebase = false
        var isInitFirebaseRemote = false
        var isInitAppsFlyer = false
        var isInitUI = false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        loadProgress = findViewById(R.id.load_text)
        Amplitude.getInstance().logEvent("STARTED_SPLASH_VIEW");

        Glide.with(applicationContext)
            .asGif()
            .load(R.drawable.splash)
            .into(findViewById(R.id.splash_anim))

        startLoad()
    }

    private fun startLoad(progress: Int = 0) {
        loadProgress(progress)
        Handler(Looper.getMainLooper()).postDelayed({
            if(progress<100) {
                startLoad(progress + 1)
            }else if(progress>=100){
                nextScreen()
            }
        }, 15)
    }

    private fun loadProgress(progress: Int = 0) {
        when (progress) {
            0 -> {
                loadProgress.text = "Preparation database"
            }
            25 -> {
                loadProgress.text = "Preparation settings"
            }
            50 -> {
                loadProgress.text = "Preparation protocol secure"
            }
            75 -> {
                loadProgress.text = "Preparation completed"
            }
        }
    }

    fun nextScreen(){
        startActivity(Intent(this, CleanerActivity::class.java))
//        startActivity(Intent(this, FirstPrerollActivity::class.java))
        finish()
    }
}