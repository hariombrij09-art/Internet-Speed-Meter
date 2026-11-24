package com.speedmeter.internet

import android.app.Service
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.net.TrafficStats
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import android.content.Context
import android.content.pm.ServiceInfo

class SpeedService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var speedView: TextView

    private val handler = Handler(Looper.getMainLooper())

    private var lastRx = 0L
    private var lastTx = 0L

    private val updateRunnable = object : Runnable {
        override fun run() {

            val rx = TrafficStats.getTotalRxBytes()
            val tx = TrafficStats.getTotalTxBytes()

            val download = if (lastRx > 0 && rx >= lastRx) {
                rx - lastRx
            } else 0L

            val upload = if (lastTx > 0 && tx >= lastTx) {
                tx - lastTx
            } else 0L

            lastRx = rx
            lastTx = tx

            speedView.text =
                "↓ ${formatSpeed(download)}  ↑ ${formatSpeed(upload)}"

            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate() {
        super.onCreate()

        createNotification()

        windowManager =
            getSystemService(Context.WINDOW_SERVICE) as WindowManager

        speedView = TextView(this).apply {
            text = "↓ 0 KB/s  ↑ 0 KB/s"
            textSize = 7f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.argb(150, 0, 0, 0))
            setPadding(2, 2, 2, 2)
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 10
        params.y = 0

        windowManager.addView(speedView, params)

        lastRx = TrafficStats.getTotalRxBytes()
        lastTx = TrafficStats.getTotalTxBytes()

        handler.postDelayed(updateRunnable, 1000)
    }

    private fun formatSpeed(bytes: Long): String {
        return if (bytes >= 1024 * 1024) {
            String.format("%.2f MB/s", bytes / 1048576.0)
        } else {
            String.format("%.1f KB/s", bytes / 1024.0)
        }
    }

    private fun createNotification() {

        val channelId = "speed_meter_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Internet Speed Meter",
                NotificationManager.IMPORTANCE_LOW
            )

            val manager =
                getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(channel)
        }

        val notification = Notification.Builder(this, channelId)
            .setContentTitle("Internet Speed Meter")
            .setContentText("Internet speed monitoring is active")
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .build()

        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(
                1,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(1, notification)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        handler.removeCallbacks(updateRunnable)

        if (::speedView.isInitialized) {
            windowManager.removeView(speedView)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
