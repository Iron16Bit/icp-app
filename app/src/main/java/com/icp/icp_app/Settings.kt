package com.icp.icp_app

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import java.io.File

class Settings : AppCompatActivity() {

    // Function used to check if needed permissions have been granted. If not, requests them
    private fun checkAndRequestPermissions(): Boolean {
        val permissions = Permissions()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {

            permissions.checkAndRequestPermissions(this)

        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissions.checkAndRequestPermissions(this)

            if (!Environment.isExternalStorageManager()) {
                val intent: Intent =
                    Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        .setData(Uri.parse("package:$packageName"))
                startActivity(intent)
            }

            return true
        } else {
            permissions.checkAndRequestPermissions(this)

            if (!Environment.isExternalStorageManager()) {
                val intent: Intent =
                    Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        .setData(Uri.parse("package:$packageName"))
                startActivity(intent)
            }

            return true
        }
        return false
    }

    private var sharedPref: SharedPreferences? = null

    private fun updateAllColors() {
        if (sharedPref?.contains("actual_theme") == true) {
            val theme = sharedPref!!.getString("actual_theme", null)

            val background = findViewById<RelativeLayout>(R.id.Background)
            background.setBackgroundColor(Color.parseColor(if (theme == "dark") { "#26364E" } else { "#ffffff" }))

            val containerBg = findViewById<RelativeLayout>(R.id.ContainerBackground)
            containerBg.setBackgroundColor(Color.parseColor(if (theme == "dark") { "#182436" } else { "#d8cedb" }))

            val title1 = findViewById<TextView>(R.id.Title1)
            title1.setTextColor(Color.parseColor(if (theme == "dark") { "#ffffff" } else { "#000000" }))

            val text1 = findViewById<TextView>(R.id.PermissionsTitle)
            text1.setTextColor(Color.parseColor(if (theme == "dark") { "#ffffff" } else { "#000000" }))

            val title2 = findViewById<TextView>(R.id.Title2)
            title2.setTextColor(Color.parseColor(if (theme == "dark") { "#ffffff" } else { "#000000" }))

            val text2 = findViewById<TextView>(R.id.DirectoryTitle)
            text2.setTextColor(Color.parseColor(if (theme == "dark") { "#ffffff" } else { "#000000" }))
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = this.getSharedPreferences("my_pref", MODE_PRIVATE)
        setContentView(R.layout.activity_settings)
        updateAllColors()

        val permissionsButton = findViewById<Button>(R.id.Permissions)
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

        val directoryButton = findViewById<Button>(R.id.Directory)
        directoryButton.setOnClickListener {
            val f = File(Environment.getExternalStorageDirectory().absolutePath + "/MyFiles/")
            if (f.exists()) {
                f.deleteRecursively()
            }
            val permissions = Permissions()
            permissions.initDir(this)
        }

        val themeButton = findViewById<Button>(R.id.Theme)
        themeButton.setOnClickListener {
            if (sharedPref?.contains("actual_theme") == true) {
                val theme = sharedPref!!.getString("actual_theme", null)

                var newTheme = ""
                newTheme += if (theme == "dark") {
                    "light"
                } else {
                    "dark"
                }

                val editor = sharedPref?.edit()
                if (editor != null) {
                    editor.putString("actual_theme", newTheme)
                    editor.apply()
                }

                updateAllColors()
            }
        }

        val prev = findViewById<ImageButton>(R.id.Back)
        prev.setOnClickListener{
            val intentMain = Intent(
                this,
                ImportSlides::class.java
            )
            this.startActivity(intentMain)
        }
    }
}