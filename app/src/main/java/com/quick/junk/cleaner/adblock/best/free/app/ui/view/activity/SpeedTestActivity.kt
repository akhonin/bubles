package com.quick.junk.cleaner.adblock.best.free.app.ui.view.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.amplitude.api.Amplitude
import com.quick.junk.cleaner.adblock.best.free.app.R
import com.quick.junk.cleaner.adblock.best.free.app.speedtest.Speedtest
import com.quick.junk.cleaner.adblock.best.free.app.speedtest.SpeedtestConfig
import com.quick.junk.cleaner.adblock.best.free.app.speedtest.TelemetryConfig
import com.quick.junk.cleaner.adblock.best.free.app.speedtest.TestPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class SpeedTestActivity: AppCompatActivity() {

    lateinit var startBtn: LinearLayout
    lateinit var pingValue: TextView
    lateinit var uploadValue: TextView
    lateinit var provider: TextView
    lateinit var ip: TextView
    lateinit var downloadValue: TextView
    lateinit var status: TextView
    lateinit var speedShower: ImageView
    var lastDownload = 0
    var lastUpload = 0
    var prevAngle = 0f
    var isAnim = false

    var test_started = false
    val st: Speedtest = Speedtest()

    val amplitide = Amplitude.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speed_test)

        findViewById<View>(R.id.icon).setOnClickListener {
            finish()
        }

        startBtn = findViewById(R.id.start_test)
        pingValue = findViewById(R.id.ping_value)
        uploadValue = findViewById(R.id.upload_value)
        provider = findViewById(R.id.provide_value)
        downloadValue = findViewById(R.id.download_value)
        speedShower = findViewById(R.id.speed_shower)
        ip = findViewById(R.id.ip_value)
        status = findViewById(R.id.status)

        startBtn.setOnClickListener {
            amplitide.logEvent("CLICK_TO_SPEED_TEST")
            page_test()
            status.text = "STOP"
        }

        st.setTelemetryConfig(TelemetryConfig(JSONObject()))
        st.setSpeedtestConfig(SpeedtestConfig(JSONObject()))

        st.addTestPoints(arrayOf(
            TestPoint(
            "Johannesburg, South Africa (Host Africa)",
            "//za1.backend.librespeed.org/",
            "garbage.php",
            "empty.php",
            "empty.php",
            "getIP.php"
        ),TestPoint(
            "Nottingham, England (LayerIP)",
            "https://uk1.backend.librespeed.org",
            "garbage.php",
            "empty.php",
            "empty.php",
            "getIP.php"
        ),TestPoint(
            "Chicago, United States (HostHatch)",
            "https://il1.us.backend.librespeed.org",
            "garbage.php",
            "empty.php",
            "empty.php",
            "getIP.php"
        )
        ))


        st.selectServer(object : Speedtest.ServerSelectedHandler(){
            override fun onServerSelected(server: TestPoint?) {
                println("SpeedTest onServerSelected ${server?.name}")
            }

        })
    }

    fun uploadRate(prog:Int): Int {
        return if(prog<=5){
            prog*30
        } else if (prog <= 10) {
            (prog * 6)  + 30
        } else if (prog <= 30) {
            ((prog - 10) * 3)  + 90
        } else if (prog <= 50) {
            ((prog - 30) * 1.5).toInt()  + 150
        } else if (prog <= 100) {
            ((prog - 50) * 1.2).toInt() + 180
        }else{
            0
        }
    }

    fun progress(progress:Int){
        if(isAnim)return
        val next = uploadRate(progress)
        val rotate = RotateAnimation(prevAngle,
            next.toFloat(),
            Animation.RELATIVE_TO_SELF,
            0.8f,
            Animation.RELATIVE_TO_SELF,
            0.28f)
        rotate.interpolator = AccelerateInterpolator()
        rotate.duration = 500
        prevAngle = next.toFloat()

        rotate.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
                isAnim = true
            }

            override fun onAnimationEnd(p0: Animation?) {
                isAnim = false
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }

        })

        speedShower.startAnimation(rotate)

    }

    fun page_test(){
        if(test_started)return
        test_started = true
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Default) {
                st.start(object: Speedtest.SpeedtestHandler(){
                    override fun onDownloadUpdate(dl: Double, progress: Double) {
                        Handler(Looper.getMainLooper()).post {
//                            speed.text = dl.toInt().toString()
                            if(dl.toInt()>0){
                                lastDownload = dl.toInt()
                            }
                            if(progress==1.0){
                                progress(0)
                            }else{
                                progress(dl.toInt())
                            }
                        }
                    }

                    override fun onUploadUpdate(ul: Double, progress: Double) {
                        if(ul.toInt()>0){
                            lastUpload = ul.toInt()
                        }
                        Handler(Looper.getMainLooper()).post {
//                            speed.text = ul.toInt().toString()
                            if(progress==0.0){
                                prevAngle = 0f
                                progress(0)
                            }else if(progress==1.0){
                                prevAngle = 0f
                                progress(0)
                            }else{
                                progress(ul.toInt())
                            }
                        }
                    }

                    override fun onPingJitterUpdate(
                        ping: Double,
                        jitter: Double,
                        progress: Double,
                    ) {
                        Handler(Looper.getMainLooper()).post {
                            pingValue.text = ping.toInt().toString()
//                            status.text = "Testing ping"
                        }
                    }

                    override fun onIPInfoUpdate(ipInfo: String) {
                        Handler(Looper.getMainLooper()).post {
                            provider.text = ipInfo.split("-")[1]
                            ip.text = ipInfo.split("-")[0]
                        }
                    }

                    override fun onTestIDReceived(id: String?, shareURL: String?) {
                        println("SpeedTest onTestIDReceived ${id} ${shareURL}")
                    }

                    override fun onEnd() {
                        Handler(Looper.getMainLooper()).post {
                            status.text = "Start again"
                            uploadValue.text = lastUpload.toString()
                            downloadValue.text = lastDownload.toString()
                            test_started =false
                        }
                        prevAngle = 0f
                    }

                    override fun onCriticalFailure(err: String?) {
                        println("SpeedTest onCriticalFailure ${err} ")
                        Handler(Looper.getMainLooper()).post {
                            status.text = "Start again"
//                            status.text = "Failure"
//                            startBtn.setTextColor(resources.getColor(R.color.white))
//                            startBtn.text = "START AGAIN"
                        }
                        prevAngle = 0f
                    }

                })
            }
        }
    }
}