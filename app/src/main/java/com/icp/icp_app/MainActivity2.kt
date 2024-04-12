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

class MainActivity2 : AppCompatActivity() {
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
    private fun updateAvailableLanguages() {
        val textView = findViewById<TextView>(R.id.AvailableLanguages)
        val folder = File(Environment.getExternalStorageDirectory().absolutePath + "/MyFiles/")

        var text = "Available languages:\n"

        val availableFiles = folder.list()
        for (f in availableFiles!!) {
            if (f.endsWith(".js") && !f.startsWith("reveal")) {
                val tmp = f.split(".")
                text += "- " + tmp[0] + "\n"
            }
        }

        textView.text = text
    }

    var Language: String? = null
    private val getContentLanguage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val tmp = uri.path.toString().split(":")
            Language = Environment.getExternalStorageDirectory().absolutePath + "/" + tmp[tmp.size-1]
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_languages)

        updateAvailableLanguages()

        val selectLanguage = findViewById<Button>(R.id.SelectLanguage)
        selectLanguage.setOnClickListener {
            getContentLanguage.launch("*/*")
        }

        val importLanguage = findViewById<Button>(R.id.ImportLanguage)
        importLanguage.setOnClickListener {
            if (Language != null) {
                val copy = CopyInit()
                val dstString = Environment.getExternalStorageDirectory().absolutePath + "/MyFiles/"

                val tmp = Language!!.split("/")
                val languageName = tmp[tmp.size-1].split(".")

                val languageFile = File(dstString + tmp[tmp.size-1])
                if (languageFile.exists()) {
                    languageFile.delete()
                }

                copy.copyExternal(Language!!, dstString, tmp[tmp.size-1])
                Toast.makeText(this, "Imported " + languageName[0], Toast.LENGTH_SHORT).show()
                updateAvailableLanguages()
            } else {
                Toast.makeText(this, "No language has been selected", Toast.LENGTH_SHORT).show()
            }
        }

        val next = findViewById<ImageButton>(R.id.NextFromLanguages)
        next.setOnClickListener{
            val intentMain = Intent(
                this,
                MainActivity3::class.java
            )
            val options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_from_left, R.anim.slide_from_right).toBundle()
            this.startActivity(intentMain, options)
        }

        val prev = findViewById<ImageButton>(R.id.PrevFromLanguages)
        prev.setOnClickListener{
            val intentMain = Intent(
                this,
                MainActivity::class.java
            )
            val options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_from_right, R.anim.slide_from_left).toBundle()
            this.startActivity(intentMain, options)
        }

        val info = findViewById<ImageButton>(R.id.Info)
        info.setOnClickListener{
            val showPopUp = PopUpFragment()
            showPopUp.show((this as AppCompatActivity).supportFragmentManager, "showPopUp")
        }

        val permissionsButton = findViewById<ImageButton>(R.id.Permissions)
        permissionsButton.setOnClickListener {
            val permissions = Permissions()
            val permissionState = permissions.checkPermissions(this)
            if (permissionState) {
                Toast.makeText(this, "Permissions already granted", Toast.LENGTH_SHORT).show()
            }

            checkAndRequestPermissions()
            val copy = CopyInit()
            copy.copy(this)
        }
    }
}