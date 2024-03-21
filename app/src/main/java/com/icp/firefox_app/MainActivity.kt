package com.icp.firefox_app

import android.Manifest
import android.Manifest.permission.INTERNET
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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
            Toast.makeText(this, "Storage permissions granted", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Storage permissions not granted!", Toast.LENGTH_SHORT).show()
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
                        .setMessage("Storage permission is needed in order for the app to work")
                        .setNegativeButton("Cancel"){dialog,_->
                            Toast.makeText(this, "Storage permission denied!", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show()
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
        setContentView(R.layout.init_activity)

        val permission: Button? = findViewById(R.id.permissions)
        permission?.setOnClickListener {
            requestPermissions()
        }

        val storage: Button? = findViewById(R.id.storage)
        storage?.setOnClickListener {
            val magic = CopyInit();
            magic.copy(this);

            Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show()
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

        val start: Button? = findViewById(R.id.start)
        start?.setOnClickListener {
            setContentView(R.layout.activity_main)
            setupGeckoView()
        }
    }
}