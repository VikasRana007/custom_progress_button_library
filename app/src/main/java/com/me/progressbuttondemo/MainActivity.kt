package com.me.progressbuttondemo

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.me.myprogressbutton.CProgressButton
import com.me.myprogressbutton.STATE

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CProgressButton.initStatusString(
            arrayOf(
                "download",
                "pause",
                "complete",
                "error",
                "delete"
            )
        )
        button1()
        button2()
        button3()
    }

    private fun button3() {
        val progressButton: CProgressButton = findViewById<View>(R.id.btn3) as CProgressButton
        val tv = findViewById<View>(R.id.state3) as TextView
        progressButton.setOnClickListener(View.OnClickListener {
            val valueAnimator = ValueAnimator.ofInt(0, 100)
            if (progressButton.getState() ===  STATE.NORMAL) {
                progressButton.startDownLoad()
                valueAnimator.duration = 5000
                valueAnimator.addUpdateListener { animation ->
                    val value = animation.animatedValue as Int
                    tv.text = "state progress:$value"
                    if (value == 100) {
                        progressButton.normal(2)
                        tv.text = "state normal"
                    }
                    progressButton.download(value)
                }
                valueAnimator.start()
            } else {
                valueAnimator.cancel()
                progressButton.normal(0)
                tv.text = "state normal"
            }
        })
    }

    private fun button2() {
        val progressButton: CProgressButton = findViewById<View>(R.id.btn2) as CProgressButton
        val tv = findViewById<View>(R.id.state2) as TextView
        progressButton.setOnClickListener {
            val valueAnimator = ValueAnimator.ofInt(0, 100)
            if (progressButton.getState() === STATE.NORMAL) {
                valueAnimator.duration = 5000
                valueAnimator.addUpdateListener { animation ->
                    val value = animation.animatedValue as Int
                    tv.text = "state progress:$value"
                    if (value == 100) {
                        progressButton.normal(2, false)
                        tv.text = "state normal"
                    }
                    progressButton.download(value)
                }
                valueAnimator.start()
            } else {
                valueAnimator.cancel()
                progressButton.normal(0, false)
                tv.text = "state normal"
            }
        }
    }

    private fun button1() {
        val progressButton: CProgressButton = findViewById<View>(R.id.btn) as CProgressButton
        val tv = findViewById<View>(R.id.state) as TextView
        progressButton.setOnClickListener {
            val valueAnimator = ValueAnimator.ofInt(0, 100)
            if (progressButton.getState() === STATE.NORMAL) {
                progressButton.startDownLoad()
                valueAnimator.duration = 5000
                valueAnimator.addUpdateListener { animation ->
                    val value = animation.animatedValue as Int
                    tv.text = "state progress:$value"
                    if (value == 100) {
                        progressButton.normal(2)
                        tv.text = "state normal"
                    }
                    progressButton.download(value)
                }
                valueAnimator.start()
            } else {
                valueAnimator.cancel()
                progressButton.normal(4)
                tv.text = "state normal"
            }
        }
    }
}