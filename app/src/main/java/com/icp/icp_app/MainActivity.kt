package com.icp.icp_app

import PathUtil
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File


class MainActivity : AppCompatActivity() {
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
    private var HTML: String? = getLastHtml()

    private val getContentHTML = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {  // Check if Oreo (8) is good or it should be reduced to Marshmallow (6)
            HTML = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                PathUtil.getPath(this, uri)
            } else {
                val tmp = uri.path.toString().split(":")
                Environment.getExternalStorageDirectory().absolutePath + "/" + tmp[tmp.size-1]
            }

            val text = findViewById<TextView>(R.id.importInfo)
            text.text = HTML

            val box = findViewById<RelativeLayout>(R.id.infoContainer)
            box.visibility = VISIBLE
            updateColor(findViewById(R.id.ImportHTML))

            val title = findViewById<TextView>(R.id.importTitle)
            val text2 = "SELECTED:"
            title.text = text2
        }
    }

    private fun getLastHtml(): String? {
        if (sharedPref?.contains("selected_html") == true) {
            val lastHtml = sharedPref!!.getString("selected_html", null)
            val box = findViewById<RelativeLayout>(R.id.infoContainer)
            box.visibility = VISIBLE

            val text = findViewById<TextView>(R.id.importInfo)
            text.text = lastHtml

            return lastHtml
        }
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPref = this.getSharedPreferences("my_pref", MODE_PRIVATE)
        setContentView(R.layout.activity_slides)

        // Unused value needed to actually show the last selected HTML
        val tmp = getLastHtml()

        // Requests permission
        val permissions = Permissions()
        if (checkAndRequestPermissions()) {
            permissions.initDir(this)
        }

        val info = findViewById<ImageButton>(R.id.Info)
        info.setOnClickListener{
            val showPopUp = PopUpFragment()
            showPopUp.show((this as AppCompatActivity).supportFragmentManager, "showPopUp")
        }

        val importHTML = findViewById<Button>(R.id.ImportHTML)
        importHTML.setOnClickListener {
            if (HTML != null) {
                val copy = CopyInit()
                val dstString = Environment.getExternalStorageDirectory().absolutePath + "/MyFiles/"

                val indexFile = File(dstString + "index.html")
                if (indexFile.exists()) {
                    indexFile.delete()
                }

                copy.copyExternal(HTML!!, dstString, "index.html")

                val editor = sharedPref?.edit()
                if (editor != null) {
                    editor.putString("selected_html", HTML)
                    editor.apply()
                }

                Toast.makeText(this@MainActivity, "Imported HTML", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "No HTML has been selected", Toast.LENGTH_SHORT).show()
            }
        }

        val selectHTML = findViewById<Button>(R.id.SelectHTML)
        selectHTML.setOnClickListener {
            getContentHTML.launch("text/html")
        }

        val next = findViewById<ImageButton>(R.id.NextFromHTML)
        next.setOnClickListener{
            val intentMain = Intent(
                this,
                MainActivity2::class.java
            )

            val dstString = Environment.getExternalStorageDirectory().absolutePath + "/MyFiles/"
            val indexFile = File(dstString + "index.html")
            if (indexFile.exists()) {
                this.startActivity(intentMain)
            } else {
                Toast.makeText(this, "No HTML slides imported", Toast.LENGTH_SHORT).show()
            }
        }

        val permissionsButton = findViewById<ImageButton>(R.id.Permissions)
        permissionsButton.setOnClickListener {
            if (checkAndRequestPermissions()) {
                Toast.makeText(this, "Permissions already granted", Toast.LENGTH_SHORT).show()
            }
            permissions.initDir(this)
        }
    }

    private fun updateColor(btn: Button?) {
        if (HTML != null) {
            btn?.background?.setTint(Color.parseColor("#604D9B"))
        }
    }
}