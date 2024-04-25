package com.icp.icp_app

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity3 : AppCompatActivity() {
    val REQUEST_ID_MULTIPLE_PERMISSIONS = 1

    // Function used to check if needed permissions have been granted. If not, requests them
    private fun checkAndRequestPermissions(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            // Storage
            val writeStorage =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val readStorage =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

            // Internet
            val internet = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)

            val listPermissionsNeeded: MutableList<String> = ArrayList()
            if (writeStorage != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            if (internet != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.INTERNET)
            }
            if (readStorage != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(
                    this,
                    listPermissionsNeeded.toTypedArray<String>(),
                    REQUEST_ID_MULTIPLE_PERMISSIONS
                )
                return false
            }
            return true
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val internet = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
            val foreground = ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE)
            val notification = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)

            val listPermissionsNeeded: MutableList<String> = ArrayList()

            if (internet != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.INTERNET)
            }

            if (foreground != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.FOREGROUND_SERVICE)
            }

            if (notification != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS)
            }

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(
                    this,
                    listPermissionsNeeded.toTypedArray<String>(),
                    REQUEST_ID_MULTIPLE_PERMISSIONS
                )
                return false
            }

            if(!Environment.isExternalStorageManager()) {
                val intent: Intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    .setData(Uri.parse("package:$packageName"))
                startActivity(intent)
            }

            return true;
        } else {
            val internet = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
            val foregroundSpecial = ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_SPECIAL_USE)
            val foreground = ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE)
            val notification = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)

            val listPermissionsNeeded: MutableList<String> = ArrayList()

            if (internet != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.INTERNET)
            }

            if (foreground != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.FOREGROUND_SERVICE)
            }

            if (foregroundSpecial != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.FOREGROUND_SERVICE_SPECIAL_USE)
            }

            if (notification != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS)
            }

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(
                    this,
                    listPermissionsNeeded.toTypedArray<String>(),
                    REQUEST_ID_MULTIPLE_PERMISSIONS
                )
                return false
            }

            if(!Environment.isExternalStorageManager()) {
                val intent: Intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    .setData(Uri.parse("package:$packageName"))
                startActivity(intent)
            }

            return true;
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_slides)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("running_channel", "Running Notification", NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val openBrowser = findViewById<Button>(R.id.open)
        openBrowser.setOnClickListener {
            val url = "http://localhost:8080"
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(url)
            )
            val chooseIntent = Intent.createChooser(intent, "Choose from below")
            startActivity(chooseIntent)
        }

        val copyButton = findViewById<ImageButton>(R.id.copy)
        copyButton.setOnClickListener {
            val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val textCopied = findViewById<TextView>(R.id.importInfo).text
            clipboardManager.setPrimaryClip(ClipData.newPlainText   ("", textCopied))
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
                Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
        }

        val prev = findViewById<ImageButton>(R.id.PrevFromShow)
        prev.setOnClickListener{
            val intentMain = Intent(
                this,
                MainActivity2::class.java
            )
            this.startActivity(intentMain)
        }

        val info = findViewById<ImageButton>(R.id.Info)
        info.setOnClickListener{
            val showPopUp = PopUpFragment()
            showPopUp.show((this as AppCompatActivity).supportFragmentManager, "showPopUp")
        }

        val permissionsButton = findViewById<ImageButton>(R.id.Permissions)
        permissionsButton.setOnClickListener {
            val permissions = Permissions()
            val permissionState = permissions.checkPermissions(this)
            if (permissionState) {
                Toast.makeText(this, "Permissions already granted", Toast.LENGTH_SHORT).show()
            } else {
                checkAndRequestPermissions()
            }
            permissions.initDir(this)
        }

        val startServer = findViewById<Button>(R.id.server)
        startServer.setOnClickListener {
            Intent(applicationContext, ServerService::class.java).also {
                it.action = ServerService.Actions.START.toString()
                startService(it)
            }

            openBrowser.visibility = VISIBLE
            findViewById<RelativeLayout>(R.id.infoContainer).visibility = VISIBLE
            findViewById<TextView>(R.id.or).visibility = VISIBLE
        }
    }
}