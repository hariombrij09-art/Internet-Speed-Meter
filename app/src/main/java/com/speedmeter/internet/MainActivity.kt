package com.speedmeter.internet

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.provider.Settings
import android.net.Uri
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.setPadding(30, 30, 30, 30)
        layout.setBackgroundColor(Color.WHITE)

        val title = TextView(this)
        title.text = "INTERNET SPEED METER"
        title.textSize = 24f
        title.setTextColor(Color.BLACK)
        title.gravity = Gravity.CENTER

        val subtitle = TextView(this)
        subtitle.text = "Live Download & Upload Speed"
        subtitle.textSize = 16f
        subtitle.setTextColor(Color.DKGRAY)
        subtitle.gravity = Gravity.CENTER

        val start = Button(this)
        start.text = "START SPEED INDICATOR"

        val stop = Button(this)
        stop.text = "STOP SPEED INDICATOR"

        layout.addView(title)
        layout.addView(subtitle)
        layout.addView(start)
        layout.addView(stop)

        setContentView(layout)

        start.setOnClickListener {

            if (!Settings.canDrawOverlays(this)) {

                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )

                startActivity(intent)

            } else {

                startService(
                    Intent(this, SpeedService::class.java)
                )
            }
        }

        stop.setOnClickListener {
            stopService(
                Intent(this, SpeedService::class.java)
            )
        }
    }

    override fun onResume() {
        super.onResume()

        if (Settings.canDrawOverlays(this)) {
            // Permission available
        }
    }
}
