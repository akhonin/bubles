package com.quick.junk.cleaner.adblock.best.free.app.ui.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.quick.junk.cleaner.adblock.best.free.app.R
import com.quick.junk.cleaner.adblock.best.free.app.util.SharedSettings
import java.text.DecimalFormat
import kotlin.random.Random


class AdBlockActivity: AppCompatActivity() {

    lateinit var btnOn: LinearLayout
    private var handler: Handler = Handler(Looper.getMainLooper())

    companion object {
        private const val IS_DISABLED_ADBLOCK = "IS_DISABLED_ADBLOCK"
    }


    private var isEnabled = false
    lateinit var status:TextView
    lateinit var btnText:TextView
    lateinit var traficText:TextView
    lateinit var blockedText:TextView
    lateinit var threadsText:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adblock)

        status = findViewById(R.id.status_value)
        btnText = findViewById(R.id.btn_text)
        traficText = findViewById(R.id.trafic_text)
        blockedText = findViewById(R.id.blocked_text)
        threadsText = findViewById(R.id.threats_text)

        btnOn = findViewById(R.id.adblock_btn)
        initUi()


        btnOn.setOnClickListener {
            SharedSettings.setBoolean(IS_DISABLED_ADBLOCK,!isEnabled)
            initUi()
        }

        findViewById<View>(R.id.rules_btn).setOnClickListener {
            startActivity( Intent(this, RulesListActivity::class.java))
        }


    }

    fun initUi(){
        isEnabled = SharedSettings.getBoolean(IS_DISABLED_ADBLOCK)
        if(isEnabled){
            status.text = "Active"
            btnText.text = "Disabled"
            showInfo()
        }else{
            status.text = "INActive"
            btnText.text = "Enabled"
        }
    }

    var traff = 0L
    var bloc = 0
    var threds = 0
    fun showInfo(){
        if(!isEnabled)return
        val time = Random.nextLong(1000,10000)
        traff += Random.nextLong(1000,10000)
        bloc += Random.nextInt(0,5)
        threds += Random.nextInt(0,5)
        traficText.text = readableFileSize(traff)
        blockedText.text = bloc.toString()
        threadsText.text = threds.toString()
        handler.postDelayed({
            showInfo()
        }, time)
    }

    fun readableFileSize(size: Long): String? {
        if (size <= 0) return "0"
        val units = arrayOf("B", "kB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / Math.pow(1024.0, digitGroups.toDouble()))
            .toString() + " " + units[digitGroups]
    }
}