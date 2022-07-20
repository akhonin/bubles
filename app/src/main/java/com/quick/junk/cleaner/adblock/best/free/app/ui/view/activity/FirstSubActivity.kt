package com.quick.junk.cleaner.adblock.best.free.app.ui.view.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.TextureView
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.quick.junk.cleaner.adblock.best.free.app.R

class FirstSubActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_sub)

        findViewById<View>(R.id.close).setOnClickListener {
            startActivity(Intent(this, MaineActivity::class.java))
            finish()
        }

        val privacyPolicyLink = findViewById<TextView>(R.id.grey_privacy)

        privacyPolicyLink.text = Html.fromHtml(getString(R.string.privacy_policy_link), Html.FROM_HTML_MODE_LEGACY)
        privacyPolicyLink.movementMethod = LinkMovementMethod.getInstance()
    }
}