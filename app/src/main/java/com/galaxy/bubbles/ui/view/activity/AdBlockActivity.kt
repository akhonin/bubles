package com.galaxy.bubbles.ui.view.activity

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.galaxy.bubbles.R
import com.galaxy.bubbles.service.LocalVpnService


class AdBlockActivity: AppCompatActivity() {

    lateinit var btnOn: LinearLayout

    companion object {
        private const val VPN_REQUEST_CODE = 0x0F
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adblock)

        btnOn = findViewById(R.id.adblock_btn)

        btnOn.setOnClickListener {
            startVpn()
        }
    }

    fun enableAdblock(){
        val intent = VpnService.prepare(applicationContext)
        if (intent != null) {
            startActivityForResult(intent, 0)
        } else {
            onActivityResult(0, RESULT_OK, null)
        }
    }

    fun stopVpn(){
        println("Stop VPN")
        val intent = Intent(this, LocalVpnService::class.java)
        intent.putExtra("COMMAND", "STOP")
        startService(intent)
    }

    fun startVpn(){
        println("Start VPN")
        val intent= VpnService.prepare(this)
        if (intent!=null){
            startActivityForResult(intent, VPN_REQUEST_CODE);
        }else{
            onActivityResult(VPN_REQUEST_CODE, Activity.RESULT_OK, null);
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VPN_REQUEST_CODE && resultCode== Activity.RESULT_OK){
            val intent = Intent(this, LocalVpnService::class.java)
            startService(intent)
        }
    }
}