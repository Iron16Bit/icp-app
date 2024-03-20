package com.icp.firefox_app

import android.Manifest
import android.Manifest.permission.INTERNET
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoSessionSettings
import org.mozilla.geckoview.GeckoView
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {

    // Values used when requesting permissions
    private val readExternal= Manifest.permission.READ_EXTERNAL_STORAGE
    private val writeExternal= Manifest.permission.WRITE_EXTERNAL_STORAGE
    private val internet = INTERNET
    private val permissions = arrayOf(
        readExternal, writeExternal, internet
    )

    // Checks whether the permission has been granted
    private val storagePermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){permissionMap->
        if (permissionMap.all { it.value }){
            Toast.makeText(this, "Media permissions granted", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Media permissions not granted!", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to request the permissions
    private fun requestPermissions(){
        //check the API level
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU){
            //filter permissions array in order to get permissions that have not been granted
            val notGrantedPermissions=permissions.filterNot { permission->
                ContextCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_GRANTED
            }
            if (notGrantedPermissions.isNotEmpty()){
                //check if permission was previously denied and return a boolean value
                val showRationale=notGrantedPermissions.any { permission->
                    shouldShowRequestPermissionRationale(permission)
                }
                //if true, explain to user why granting this permission is important
                if (showRationale){
                    AlertDialog.Builder(this)
                        .setTitle("Storage Permission")
                        .setMessage("Storage permission is needed in order to show images and videos")
                        .setNegativeButton("Cancel"){dialog,_->
                            Toast.makeText(this, "Read media storage permission denied!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                        .setPositiveButton("OK"){_,_->
                            storagePermission.launch(notGrantedPermissions.toTypedArray())
                        }
                        .show()
                }else{
                    //launch the videoPermission ActivityResultContract
                    storagePermission.launch(notGrantedPermissions.toTypedArray())
                }
            }else{
                Toast.makeText(this, "Read media storage permission granted", Toast.LENGTH_SHORT).show()
            }
        }else{
            val intent: Intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                .setData(Uri.parse("package:$packageName"))
            startActivity(intent)
        }
    }

    // Values used by GeckoView
    private lateinit var geckoView: GeckoView
    private val geckoSession = GeckoSession(GeckoSessionSettings.Builder().allowJavascript(true).build())

    // Function that launches GeckoView
    private fun setupGeckoView() {
        geckoView = findViewById(R.id.geckoview)

        val runtime = GeckoRuntime.create(this)
        geckoSession.open(runtime)

        geckoView.setSession(geckoSession)

        geckoSession.loadUri("http://localhost:8080/")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val storage: Button? = findViewById(R.id.storage)
        storage?.setOnClickListener {
            requestPermissions()
            val copy = CopyFromAssets()

            val listAssets = assets.list("")?.clone()
            if (listAssets != null) {
                if (listAssets.isNotEmpty()) {
                    for (file in listAssets) {
                        copy.copyAsset(file, "/MyFiles/", "", this)
                    }
                }
            }

            val subDir1 = Environment.getExternalStorageDirectory().absolutePath + "/MyFiles/utils/"
            val dir1: File = File(subDir1)
            if (!dir1.exists()) {
                dir1.mkdirs()
            }

            val subDir2 = "$subDir1/cpp/"
            val dir2: File = File(subDir2)
            if (!dir2.exists()) {
                dir2.mkdirs()
            }

            val listAssets2 = assets.list("utils/cpp/")?.clone()
            if (listAssets2 != null) {
                if (listAssets2.isNotEmpty()) {
                    for (file in listAssets2) {
                        copy.copyAsset(file, "/MyFiles/utils/cpp/", "utils/cpp/", this)
                    }
                }
            }

            val subDir3 = "$subDir1/java/"
            val dir3: File = File(subDir3)
            if (!dir3.exists()) {
                dir3.mkdirs()
            }

            val listAssets3 = assets.list("utils/java/")?.clone()
            if (listAssets3 != null) {
                if (listAssets3.isNotEmpty()) {
                    for (file in listAssets3) {
                        copy.copyAsset(file, "/MyFiles/utils/java/", "utils/java/", this)
                    }
                }
            }

            val subDir4 = "$subDir1/python/"
            val dir4: File = File(subDir4)
            if (!dir4.exists()) {
                dir4.mkdirs()
            }

            val listAssets4 = assets.list("utils/python/")?.clone()
            if (listAssets4 != null) {
                if (listAssets4.isNotEmpty()) {
                    for (file in listAssets4) {
                        copy.copyAsset(file, "/MyFiles/utils/python/", "utils/python/", this)
                    }
                }
            }

            val subDir5 = "$subDir4/pyodide/"
            val dir5: File = File(subDir5)
            if (!dir5.exists()) {
                dir5.mkdirs()
            }

            val listAssets5 = assets.list("utils/python/pyodide/")?.clone()
            if (listAssets5 != null) {
                if (listAssets5.isNotEmpty()) {
                    for (file in listAssets5) {
                        copy.copyAsset(file, "/MyFiles/utils/python/pyodide/", "utils/python/pyodide/", this)
                    }
                }
            }
        }

        val server: Button? = findViewById(R.id.server)
        server?.setOnClickListener {
            try {
                val httpServer = Server()
                Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Failed...", Toast.LENGTH_SHORT).show()
            }
        }

        setupGeckoView()
    }
}