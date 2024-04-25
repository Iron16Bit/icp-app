package com.icp.icp_app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.io.File

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

    fun initDir(context: Context) {
        val f = File("/MyFiles")
        if (f.exists()) {
            val files = f.list()
            if (files != null) {
                if (files.size < 5) {
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