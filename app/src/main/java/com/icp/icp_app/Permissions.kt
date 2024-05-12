package com.icp.icp_app

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File

// Class that asks different permissions and in different ways in order to support all Android versions down to 5.0

class Permissions {
    fun checkPermissions(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            // Storage
            val writeStorage =
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val readStorage =
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)

            // Internet
            val internet = ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET)

            val listPermissionsNeeded: MutableList<String> = ArrayList()
            if (writeStorage != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            if (internet != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            if (readStorage != PackageManager.PERMISSION_GRANTED) {
                return false;
            }

            return true
        } else {
            val internet = ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET)

            if (internet != PackageManager.PERMISSION_GRANTED) {
                return false;
            }

            if(!Environment.isExternalStorageManager()) {
                return false;
            }

            return true;
        }
    }

    private val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
    fun checkAndRequestPermissions(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            // Storage
            val writeStorage =
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val readStorage =
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)

            // Internet
            val internet = ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET)

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
                    context as Activity,
                    listPermissionsNeeded.toTypedArray<String>(),
                    REQUEST_ID_MULTIPLE_PERMISSIONS
                )
                return false
            }
            return true
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val internet = ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET)
            val foreground = ContextCompat.checkSelfPermission(context, Manifest.permission.FOREGROUND_SERVICE)
            val notification = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)

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
                    context as Activity,
                    listPermissionsNeeded.toTypedArray<String>(),
                    REQUEST_ID_MULTIPLE_PERMISSIONS
                )
                return false
            }

            return true;
        } else {
            val internet = ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET)
            val foregroundSpecial = ContextCompat.checkSelfPermission(context, Manifest.permission.FOREGROUND_SERVICE_SPECIAL_USE)
            val foreground = ContextCompat.checkSelfPermission(context, Manifest.permission.FOREGROUND_SERVICE)
            val notification = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)

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
                    context as Activity,
                    listPermissionsNeeded.toTypedArray<String>(),
                    REQUEST_ID_MULTIPLE_PERMISSIONS
                )
                return false
            }

            return true;
        }
    }

    fun initDir(context: Context) {
        val f = File(Environment.getExternalStorageDirectory().absolutePath + "/MyFiles/")
        if (f.exists()) {
            val files = f.list()
            if (files != null) {
                if (files.size < 6) {
                    val copy = CopyInit()
                    copy.copy(context)
                    Toast.makeText(context, "App successfully initialized!", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            val copy = CopyInit()
            copy.copy(context)
            Toast.makeText(context, "App successfully initialized!", Toast.LENGTH_SHORT).show()
        }
    }
}