package com.quick.junk.cleaner.adblock.best.free.app

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.amplitude.api.Amplitude
import com.appsflyer.AppsFlyerLib
import com.facebook.FacebookSdk
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.onesignal.OneSignal
import com.quick.junk.cleaner.adblock.best.free.app.util.SharedSettings
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import java.util.*

class App: MultiDexApplication() {
    private val revenueApiKey = "goog_BUmxocVOhyXZlvtfTJxuzmbJPwj"

    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    init {
        instance = this
    }

    override fun onCreate() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        var uniqueID = SharedSettings.getString("UserUUD")
        if(uniqueID.isEmpty()){
            uniqueID = UUID.randomUUID().toString()
            SharedSettings.setString("UserUUD",uniqueID)
        }

        Amplitude.getInstance().initialize(this, "f74515d3ec38d6cc5ca60c3e0ccc9460",uniqueID).enableForegroundTracking(this)

        FacebookSdk.fullyInitialize()

        OneSignal.setExternalUserId(uniqueID)
        OneSignal.unsubscribeWhenNotificationsAreDisabled(true)
        OneSignal.initWithContext(this)

        Purchases.debugLogsEnabled = true

        val conf = PurchasesConfiguration.Builder(this, revenueApiKey)
        conf.appUserID(uniqueID)
        Purchases.configure(conf.build())



        AppsFlyerLib.getInstance().setCustomerUserId(uniqueID);
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        remoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(0)
                .build())

        remoteConfig.fetch(0).addOnCompleteListener {
            if (!it.isSuccessful) return@addOnCompleteListener
            remoteConfig.activate()
        }
        super.onCreate()
    }

    companion object {
        const val CERTIFICATES = 1
        var instance: App? = null
        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }
}