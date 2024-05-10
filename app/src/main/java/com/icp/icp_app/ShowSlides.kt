package com.icp.icp_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
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
import org.w3c.dom.Text

// The third page of the app, opens the slides in the browser

class ShowSlides : AppCompatActivity() {

    private var sharedPref: SharedPreferences? = null

    private fun updateAllColors() {
        if (sharedPref?.contains("actual_theme") == true) {
            val theme = sharedPref!!.getString("actual_theme", null)

            val background = findViewById<RelativeLayout>(R.id.Background)
            background.setBackgroundColor(Color.parseColor(if (theme == "dark") { "#26364E" } else { "#ffffff" }))

            val titleShow = findViewById<TextView>(R.id.TitleShow)
            titleShow.setTextColor(Color.parseColor(if (theme == "dark") { "#ffffff" } else { "#000000" }))

            val text = findViewById<TextView>(R.id.or)
            text.setTextColor(Color.parseColor(if (theme == "dark") { "#ffffff" } else { "#000000" }))

            val buttonBg = findViewById<Button>(R.id.open)
            buttonBg.background.setTint(Color.parseColor(if (theme == "dark") { "#182436" } else { "#d8cedb" }))

            val containerBg = findViewById<RelativeLayout>(R.id.infoBackground)
            containerBg.setBackgroundColor(Color.parseColor(if (theme == "dark") { "#182436" } else { "#d8cedb" }))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = this.getSharedPreferences("my_pref", MODE_PRIVATE)
        setContentView(R.layout.activity_show_slides)
        updateAllColors()

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
                ImportLanguages::class.java
            )
            this.startActivity(intentMain)
        }

        val info = findViewById<ImageButton>(R.id.Info)
        info.setOnClickListener{
            val showPopUp = PopUpFragment()
            showPopUp.show((this as AppCompatActivity).supportFragmentManager, "showPopUp")
        }

        val settingsButton = findViewById<ImageButton>(R.id.Permissions)
        settingsButton.setOnClickListener {
            val intentMain = Intent(
                this,
                com.icp.icp_app.Settings::class.java
            )
            this.startActivity(intentMain)
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