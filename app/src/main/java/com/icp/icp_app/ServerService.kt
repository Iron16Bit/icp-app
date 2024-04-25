package com.icp.icp_app

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat


class ServerService : Service() {

    private var server: Server? = null;

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> stop()
        }

        return START_STICKY;
    }

    private fun start() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val builder = NotificationCompat.Builder(this, "running_channel")
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Local server running")

            val stopSelf = Intent(this, ServerService::class.java)
            stopSelf.setAction(Actions.STOP.toString())
            val pStopSelf =
                PendingIntent.getService(this, 0, stopSelf,
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            builder.addAction(R.drawable.icon, "STOP", pStopSelf);

            startForeground(1, builder.build())

            server = Server()
            server!!.start()
        } else {
            val builder = NotificationCompat.Builder(this)
                .setContentTitle("Local server running")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            val stopSelf = Intent(this, ServerService::class.java)
            stopSelf.setAction(Actions.STOP.toString())
            val pStopSelf =
                PendingIntent.getService(this, 0, stopSelf,
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            builder.addAction(R.drawable.icon, "STOP", pStopSelf);

            val notification = builder.build()

            startForeground(1, notification)

            server = Server()
            server!!.start()
        }
    }

    private fun stop() {
        server?.stop()
        stopSelf()
    }

    enum class Actions {
        START, STOP
    }
}