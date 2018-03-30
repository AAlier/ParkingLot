package neobis.alier.parking.ui.splash

import android.content.Intent
import android.graphics.PixelFormat
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_splash.*
import neobis.alier.parking.R
import neobis.alier.parking.ui.main.MainActivity

class SplashActivity : AppCompatActivity() {
    private var splashThread: Thread? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        window.setFormat(PixelFormat.RGBA_8888)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        startAnimation()
    }

    private fun startAnimation() {
        var animation = AnimationUtils.loadAnimation(this, R.anim.alpha)
        animation.reset()
        layout.clearAnimation()
        layout.startAnimation(animation)

        animation = AnimationUtils.loadAnimation(this, R.anim.translate)
        animation.reset()
        logo.clearAnimation()
        logo.startAnimation(animation)

        runAnimation()
    }

    private fun runAnimation() {
        splashThread = object : Thread() {
            override fun run() {
                try {
                    var waited = 0
                    // Splash screen pause time
                    while (waited < 2000) {
                        Thread.sleep(100)
                        waited += 200
                    }
                } catch (e: InterruptedException) {
                    // do nothing
                } finally {
                    runNextView()
                }
            }
        }
        (splashThread as Thread).start()
    }

    fun runNextView(){
        startActivity(Intent(applicationContext, MainActivity::class.java))
        this.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        splashThread = null
    }
}