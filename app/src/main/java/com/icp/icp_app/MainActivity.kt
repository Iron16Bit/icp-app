package com.icp.icp_app

import PathUtil
import android.Manifest
import android.content.Context
import android.content.Intent
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
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import java.io.File


class MainActivity : AppCompatActivity() {
    private val REQUEST_ID_MULTIPLE_PERMISSIONS = 1

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

    var HTML: String? = null

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
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slides)

        // Requests permission
        val permissions = Permissions()
        var permissionState = permissions.checkPermissions(this)
        if (!permissionState) {
            checkAndRequestPermissions()
        } else {
            // If it hasn't been done, copies the main ICP files in the device's memory
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
                Toast.makeText(this@MainActivity, "Imported HTML", Toast.LENGTH_SHORT).show()

                val sharedPref = getPreferences(Context.MODE_PRIVATE)
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
            permissionState = permissions.checkPermissions(this)
            if (permissionState) {
                Toast.makeText(this, "Permissions already granted", Toast.LENGTH_SHORT).show()
            } else {
                checkAndRequestPermissions()
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