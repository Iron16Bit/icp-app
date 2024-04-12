package com.icp.icp_app

import android.Manifest
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File


class MainActivity : AppCompatActivity() {
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
                listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            if (readStorage != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION)
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
        } else {
            val internet = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)

            val listPermissionsNeeded: MutableList<String> = ArrayList()

            if (internet != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
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
        if (uri != null) {
            val tmp = uri.path.toString().split(":")
            HTML = Environment.getExternalStorageDirectory().absolutePath + "/" + tmp[tmp.size-1]
            val text = findViewById<TextView>(R.id.importInfo)
            text.text = HTML
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
            val f = File("/MyFiles")
            if (!f.exists()) {
                val files = f.list()
                if (files != null) {
                    if (files.size != 5) {
                        val copy = CopyInit()
                        copy.copy(this)
                    }
                } else {
                    val copy = CopyInit()
                    copy.copy(this)
                }
            }
            val copy = CopyInit()
            copy.copy(this)
        }

        val info = findViewById<ImageButton>(R.id.Info)
        info.setOnClickListener{
            val showPopUp = PopUpFragment()
            showPopUp.show((this as AppCompatActivity).supportFragmentManager, "showPopUp")
        }

        val selectHTML = findViewById<Button>(R.id.SelectHTML)
        selectHTML.setOnClickListener {
            getContentHTML.launch("text/html")
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
            } else {
                Toast.makeText(this@MainActivity, "No HTML has been selected", Toast.LENGTH_SHORT).show()
            }
        }

        val next = findViewById<ImageButton>(R.id.NextFromHTML)
        next.setOnClickListener{
            val intentMain = Intent(
                this,
                MainActivity2::class.java
            )
            val options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_from_left, R.anim.slide_from_right).toBundle()
            this.startActivity(intentMain, options)
        }

        val permissionsButton = findViewById<ImageButton>(R.id.Permissions)
        permissionsButton.setOnClickListener {
            permissionState = permissions.checkPermissions(this)
            if (permissionState) {
                Toast.makeText(this, "Permissions already granted", Toast.LENGTH_SHORT).show()
            }

            checkAndRequestPermissions()
            val copy = CopyInit()
            copy.copy(this)
        }
    }
}