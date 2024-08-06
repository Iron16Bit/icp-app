package com.icp.icp_app

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Button
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.PrintWriter


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

            val containerBg2 = findViewById<RelativeLayout>(R.id.ContainerBackgroundStyle)
            containerBg2.setBackgroundColor(Color.parseColor(if (theme == "dark") { "#182436" } else { "#d8cedb" }))

            val title3 = findViewById<TextView>(R.id.SizeTitle)
            title3.setTextColor(Color.parseColor(if (theme == "dark") { "#ffffff" } else { "#000000" }))

            val containerBg3 = findViewById<RelativeLayout>(R.id.ContainerNumber)
            containerBg3.setBackgroundColor(Color.parseColor(if (theme == "dark") { "#290042" } else { "#ac5eff" }))

            val containerBg4 = findViewById<RelativeLayout>(R.id.ContainerBackgroundNumber)
            containerBg4.setBackgroundColor(Color.parseColor(if (theme == "dark") { "#341352" } else { "#ad86fc" }))

            val text3 = findViewById<TextView>(R.id.TextSize)
            text3.setTextColor(Color.parseColor(if (theme == "dark") { "#ffffff" } else { "#000000" }))
        }
    }

    private fun updateText() {
        val textSize = findViewById<TextView>(R.id.TextSize)
        val path = Environment.getExternalStorageDirectory().absolutePath + "/MyFiles/textSize.json"
        val sizeJson = File(path)

        if (sizeJson.exists()) {
            val jsonContent = sizeJson.readText(Charsets.UTF_8)
            textSize.text = jsonContent
        } else {
            textSize.text = "?"
        }
    }

    private fun changeSize(op: Int) {
        val textSize = findViewById<TextView>(R.id.TextSize)
        val path = Environment.getExternalStorageDirectory().absolutePath + "/MyFiles/textSize.json"
        val sizeJson = File(path)

        if (sizeJson.exists()) {
            val jsonContent = sizeJson.readText(Charsets.UTF_8)
            var newSize = jsonContent.toInt()
            if (op == 0) newSize+=1 else newSize-=1
            if (newSize < 1) newSize=1
            val writer = PrintWriter(sizeJson)
            writer.print(newSize.toString())
            writer.close()
            updateText()
        } else {
            textSize.text = "?"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = this.getSharedPreferences("my_pref", MODE_PRIVATE)
        setContentView(R.layout.activity_settings)
        updateAllColors()
        updateText()

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

            val editor = sharedPref?.edit()
            if (editor != null) {
                editor.putString("show_tutorial", "true")
                editor.apply()
            }

            val editor2 = sharedPref?.edit()
            if (editor2 != null) {
                editor2.putString("actual_theme", "dark")
                editor2.apply()
            }
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
            val tutorial = sharedPref!!.getString("show_tutorial", null)
            if (tutorial == "false") {
                val intentMain = Intent(
                    this,
                    ImportSlides::class.java
                )
                this.startActivity(intentMain)
            } else {
                val intentMain = Intent(
                    this,
                    Tutorial::class.java
                )
                this.startActivity(intentMain)
            }
        }

        val plusButton = findViewById<Button>(R.id.SizePlus)
        plusButton.setOnClickListener {
            changeSize(0)
        }

        val minusButton = findViewById<Button>(R.id.SizeMinus)
        minusButton.setOnClickListener {
            changeSize(1)
        }

        val callback = onBackPressedDispatcher.addCallback(this) {
            prev.callOnClick()
        }
    }
}