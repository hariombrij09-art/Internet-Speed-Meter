
package com.speedmeter.internet

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.provider.Settings
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.widget.*
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable

class MainActivity : Activity() {

    private fun background(
        color: Int,
        stroke: Int = Color.TRANSPARENT
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = 24f
            if (stroke != Color.TRANSPARENT) {
                setStroke(3, stroke)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 24, 24, 24)
            setBackgroundColor(Color.BLACK)
        }

        val logo = ImageView(this).apply {
            setImageResource(R.drawable.ic_speed_logo)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }

        layout.addView(
            logo,
            LinearLayout.LayoutParams(130, 130).apply {
                bottomMargin = 25
            }
        )

        val title = TextView(this).apply {
            text = "INTERNET SPEED METER"
            textSize = 25f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }

        layout.addView(title)

        val subtitle = TextView(this).apply {
            text = "LIVE DOWNLOAD & UPLOAD SPEED"
            textSize = 15f
            setTextColor(Color.rgb(22, 139, 255))
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 40)
        }

        layout.addView(subtitle)

        val start = Button(this).apply {
            text = "START SPEED INDICATOR"
            textSize = 16f
            setTextColor(Color.WHITE)
            background = background(
                Color.rgb(0, 100, 220)
            )
        }

        layout.addView(
            start,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                60
            ).apply {
                bottomMargin = 18
            }
        )

        val stop = Button(this).apply {
            text = "STOP SPEED INDICATOR"
            textSize = 16f
            setTextColor(Color.WHITE)
            background = background(
                Color.rgb(35, 35, 35),
                Color.rgb(22, 139, 255)
            )
        }

        layout.addView(
            stop,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                60
            )
        )

        val footer = TextView(this).apply {
            text = "DOWNLOAD  ↓       UPLOAD  ↑"
            textSize = 14f
            setTextColor(Color.rgb(32, 230, 75))
            gravity = Gravity.CENTER
            setPadding(0, 45, 0, 0)
        }

        layout.addView(footer)

        setContentView(layout)

        start.setOnClickListener {

            if (!Settings.canDrawOverlays(this)) {

                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )

                startActivity(intent)

            } else {

                val serviceIntent =
                    Intent(this, SpeedService::class.java)

                if (android.os.Build.VERSION.SDK_INT >= 26) {
                    startForegroundService(serviceIntent)
                } else {
                    startService(serviceIntent)
                }
            }
        }

        stop.setOnClickListener {
            stopService(Intent(this, SpeedService::class.java))
        }
    }
}
