package com.quick.junk.cleaner.adblock.best.free.app.ui.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.amplitude.api.Amplitude
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.bumptech.glide.Glide
import com.facebook.FacebookSdk
import com.facebook.applinks.AppLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.quick.junk.cleaner.adblock.best.free.app.R
import com.quick.junk.cleaner.adblock.best.free.app.data.Config.Companion.IS_FIRST_RUN_KEY
import com.quick.junk.cleaner.adblock.best.free.app.data.Config.Companion.IS_FIRST_RUN_WITH_ALERT_KEY
import com.quick.junk.cleaner.adblock.best.free.app.data.Config.Companion.IS_FIRST_RUN_WITH_PAYWALL_KEY
import com.quick.junk.cleaner.adblock.best.free.app.data.Config.Companion.IS_FIRST_RUN_WITH_SCAN_KEY
import com.quick.junk.cleaner.adblock.best.free.app.data.Config.Companion.IS_FUNNEL
import com.quick.junk.cleaner.adblock.best.free.app.data.Config.Companion.IS_NO_VIP_PREF_KEY
import com.quick.junk.cleaner.adblock.best.free.app.data.Config.Companion.WHITE_ENABLED
import com.quick.junk.cleaner.adblock.best.free.app.util.SharedSettings
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URL

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

    private val appsFlyerId = "msBM8ReCenoFM7Qn5Lwa2a"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        loadProgress = findViewById(R.id.load_text)
        Amplitude.getInstance().logEvent("STARTED_SPLASH_VIEW");

        Glide.with(applicationContext)
            .asGif()
            .load(R.drawable.splash)
            .into(findViewById(R.id.splash_anim))

        isInitFacebook = false
        isInitFirebase = false
        isInitAppsFlyer = false
        isInitFirebaseRemote = false
        isInitUI = false

        FacebookSdk.setAutoInitEnabled(true)
        FacebookSdk.fullyInitialize()

//        Purchases.debugLogsEnabled = true
//        Purchases.sharedInstance.collectDeviceIdentifiers()
//
//        Purchases.sharedInstance.setOnesignalID("048e64ed-f3a5-42da-8a48-d1199bfbe3aa")
//        Purchases.sharedInstance.setAppsflyerID(AppsFlyerLib.getInstance().getAppsFlyerUID(this))

        if(SharedSettings.getString(IS_FUNNEL).length>2&&funnel==null){
            funnel = SharedSettings.getString(IS_FUNNEL)
            startLoad()
        }else{
            initAppsFlayer()
        }
    }

    private fun initAppsFlayer() {
        if (isInitAppsFlyer) return
        isInitAppsFlyer = true
        val conversionDataListener = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {

                if (funnel == null) funnel = p0?.get("campaign") as String?

                val eventProperties = JSONObject()
                try {
                    eventProperties.put("FUNNEL", p0.toString())
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                initFacebook(p0)

            }

            override fun onConversionDataFail(p0: String?) {
                initFacebook(null)
                println("funnel appsFlyer onConversionDataFail $p0")
            }

            override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                initFacebook(null)
                println("funnel appsFlyer onAppOpenAttribution $p0")
            }

            override fun onAttributionFailure(p0: String?) {
                initFacebook(null)
                println("funnel appsFlyer onAttributionFailure $p0")
            }
        }

        AppsFlyerLib.getInstance().setDebugLog(true)

        AppsFlyerLib.getInstance()
            .init(appsFlyerId, conversionDataListener, applicationContext)

        AppsFlyerLib.getInstance().start(this)
    }

    fun initFacebook(appsFlayerParams: MutableMap<String, Any>?){
            if(isInitFacebook)return
            isInitFacebook = true
            var clickid:String? = null;

            if(appsFlayerParams?.isNotEmpty() == true) {
                clickid = appsFlayerParams["clickid"]?.toString()

                if(appsFlayerParams["campaign"] !=null)
                    Purchases.sharedInstance.setCampaign(appsFlayerParams["campaign"]?.toString())
                if(appsFlayerParams["media_source"] !=null)
                    Purchases.sharedInstance.setMediaSource(
                        appsFlayerParams["media_source"]?.toString()
                    )
                if(appsFlayerParams["af_siteid"] !=null)
                    Purchases.sharedInstance.setAd(appsFlayerParams["af_siteid"]?.toString())

                if(clickid!=null) {
                    Purchases.sharedInstance.setAttributes(
                        mapOf(
                            "clickid" to clickid
                        )
                    )
                }
            }

            if (clickid != null
                &&SharedSettings.getBoolean(IS_FIRST_RUN_WITH_PAYWALL_KEY)
                &&SharedSettings.getBoolean(IS_FIRST_RUN_WITH_SCAN_KEY)
                &&SharedSettings.getBoolean(IS_FIRST_RUN_WITH_ALERT_KEY)
                &&SharedSettings.getBoolean(IS_FIRST_RUN_KEY))  {
                postBack(clickid)
            }

            AppLinkData.fetchDeferredAppLinkData(this) {
                println("it?.targetUri funnel ${it?.targetUri}" )
                if(funnel==null)funnel = it?.targetUri.toString()
                initFirebase()
            }
        }

    private val client = OkHttpClient()

    private fun postBack(clickId:String) {
        val url =
            URL("https://quickjunkcleaner.com/e157311/postback?subid=$clickId&lead_status=install&status=install")
        println("postBack url $url")
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    println("postBack ${response}")
                }
            }
        })
    }

    private fun initFirebase() {
        if(isInitFirebase)return
        isInitFirebase = true
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                if (pendingDynamicLinkData != null) {
                    println("pendingDynamicLinkData ${pendingDynamicLinkData.link}")
                    funnel = pendingDynamicLinkData.link.toString()
                }

                initFirebaseRemoteConfig()
            }
            .addOnFailureListener(this) { e ->
                println("dynamicLinks error $e")
                initFirebaseRemoteConfig()
            }
    }

    private fun initFirebaseRemoteConfig(){
        if(isInitFirebaseRemote)return
        isInitFirebaseRemote=true

        val remoteConfig = FirebaseRemoteConfig.getInstance()
        remoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(0)
                .build())


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
//        funnel = "scanweek"
        if(funnel==null){
            funnel = "Organic"
        }
        println("ActivitySplash funnel $funnel")

        Purchases.sharedInstance.getCustomerInfo(object : ReceiveCustomerInfoCallback {

            override fun onError(error: PurchasesError) {
                println("Purchases onError ${error}")
                startUi(funnel)
            }
            override fun onReceived(customerInfo: CustomerInfo) {
                val isHaveSubscribe = customerInfo.entitlements["premium"]?.isActive == true
                println("isHaveSubscribe ${isHaveSubscribe}")
                SharedSettings.setBoolean(IS_NO_VIP_PREF_KEY, !isHaveSubscribe)
//                isHaveSubscribe = true
                if(isHaveSubscribe)funnel=null
                startUi(funnel)
            }
        })

    }

    var mIntent:Intent? = null

    private fun startUi(funnel: String?) {
        if(isInitUI)return
        isInitUI = true

        if (SharedSettings.getBoolean(IS_FIRST_RUN_KEY)) {
            val eventProperties = JSONObject()
            try {
                eventProperties.put("FUNNEL", funnel)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            Amplitude.getInstance().logEvent("APPS_FLAYER_PARAMS", eventProperties)
        }


        if(funnel!=null&&SharedSettings.getBoolean(IS_NO_VIP_PREF_KEY)) {
            SharedSettings.setString(IS_FUNNEL, funnel)
            if(
                funnel.contains("scanweek")
                || funnel.contains("scanmonth")
                || funnel.contains("scanyear")
            ){
                if (SharedSettings.getBoolean(IS_FIRST_RUN_WITH_SCAN_KEY)) {
//                    mIntent = Intent(this, ActivityScan::class.java)
//                    mIntent!!.putExtra("funnel", funnel)
                }
            }else  if(
                funnel.contains("scan2week")
                || funnel.contains("scan2month")
                || funnel.contains("scan2year")
            ){
                if (SharedSettings.getBoolean(IS_FIRST_RUN_WITH_SCAN_KEY)) {
//                    mIntent = Intent(this, ActivityScanSecond::class.java)
//                    mIntent!!.putExtra("funnel", funnel)
                }
            }
            else if(
                funnel.contains("paywallweek")
                || funnel.contains("paywallmonth")
                || funnel.contains("paywallyear")
            ) {
                mIntent = Intent(this, FirstSubActivity::class.java)
                mIntent!!.putExtra("funnel", funnel)
            }else if(
                funnel.contains("paywall2week")
                || funnel.contains("paywall2month")
                || funnel.contains("paywall2year")
            ) {
                mIntent = Intent(this, FirstSubActivity::class.java)
                mIntent!!.putExtra("funnel", funnel)
            }
        }


        val remoteConfig = FirebaseRemoteConfig.getInstance()
        FirebaseRemoteConfig.getInstance().setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(0)
                .build())


        remoteConfig.fetch(0).addOnCompleteListener {
            if(mIntent==null)mIntent = getDefIntent(remoteConfig.getBoolean("white_enabled"))

            mIntent!!.putExtra("funnel", funnel)
            startActivity(mIntent)
            finish()
        }

    }

    private fun getDefIntent(wEnabled:Boolean):Intent {
        return if(SharedSettings.getBoolean(IS_FIRST_RUN_KEY)){
            Intent(this, FirstPrerollActivity::class.java)
        }else if(SharedSettings.getBoolean(WHITE_ENABLED)
            &&SharedSettings.getBoolean(IS_NO_VIP_PREF_KEY)
            &&wEnabled){
            Intent(this, FirstSubActivity::class.java)
        } else {
//            Intent(this, ActivityMaine::class.java)
            Intent(this, FirstSubActivity::class.java)
        }
    }
}