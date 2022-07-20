package com.quick.junk.cleaner.adblock.best.free.app.ui.view.activity

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.quick.junk.cleaner.adblock.best.free.app.R

class PremiumActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premium)
        findViewById<View>(R.id.close).setOnClickListener {
            finish()
        }

        val privacyPolicyLink = findViewById<TextView>(R.id.grey_privacy)

        privacyPolicyLink.text = Html.fromHtml(getString(R.string.privacy_policy_link), Html.FROM_HTML_MODE_LEGACY)
        privacyPolicyLink.movementMethod = LinkMovementMethod.getInstance()
    }
}