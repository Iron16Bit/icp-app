package com.icp.icp_app

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

// A service which allows to keep the local http server open in background

class ServerService : Service() {

    private var server: Server? = null

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
        val builder = NotificationCompat.Builder(this, "running_channel")
            .setSmallIcon(R.drawable.icon)
            .setContentTitle("App running in background")

        val stopSelf = Intent(this, ServerService::class.java)
        stopSelf.setAction(Actions.STOP.toString())
        val pStopSelf = PendingIntent.getService(this, 0, stopSelf,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        builder.addAction(R.drawable.icon, "STOP", pStopSelf)

        val randomInt = System.currentTimeMillis()%10000

        startForeground(randomInt.toInt(), builder.build())

        server = Server()
        server!!.launch()
    }

    private fun stop() {
        server?.stop()
        stopSelf()
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    enum class Actions {
        START, STOP
    }
}