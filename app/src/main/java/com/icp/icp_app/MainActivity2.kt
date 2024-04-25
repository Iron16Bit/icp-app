package com.icp.icp_app

import android.Manifest
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
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
    private fun updateAvailableLanguages() {
        val textView = findViewById<TextView>(R.id.AvailableLanguages)
        val folder = File(Environment.getExternalStorageDirectory().absolutePath + "/MyFiles/")

        var text = ""

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
            Language = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                PathUtil.getPath(this, uri)
            } else {
                val tmp = uri.path.toString().split(":")
                Environment.getExternalStorageDirectory().absolutePath + "/" + tmp[tmp.size-1]
            }

            val text = findViewById<TextView>(R.id.importInfo)
            text.text = Language

            val box = findViewById<RelativeLayout>(R.id.box)
            box.visibility = View.VISIBLE
            updateColor(findViewById(R.id.ImportLanguage))
        }
    }

    private fun updateColor(btn: Button?) {
        if (Language != null) {
            btn?.background?.setTint(Color.parseColor("#604D9B"))
        }
    }

    private fun getLanguagesInSlides() {
        val path = Environment.getExternalStorageDirectory().absolutePath + "/MyFiles/index.html"
        val htmlFile = File(path)

        var text = ""

        if (htmlFile.exists()) {

            val htmlContent = htmlFile.readText(Charsets.UTF_8)

            val regex = "<script src=.*></script>".toRegex()
            val scripts = regex.findAll(htmlContent)

            for (s in scripts) {
                val split1 = s.value.split("src=")
                val split2 = split1[1].split(">")
                val split3 = split2[0].split(".")
                val name = split3[0].drop(1)

                if (name != "reveal") {
                    text += "- $name\n"
                }
            }
        } else {
            text += "- None\n"
        }

        val textView = findViewById<TextView>(R.id.NeededLanguages)
        textView.text = text
    }

    private fun checkAvailability(): Boolean {
        val available = findViewById<TextView>(R.id.AvailableLanguages).text
        val needed = findViewById<TextView>(R.id.NeededLanguages).text

        return available.contains(needed)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_languages)

        getLanguagesInSlides()
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

                copy.copyExternal(Language!!, dstString, "export_languages.zip")
                val zip = File(Environment.getExternalStorageDirectory().absolutePath + "/MyFiles/export_languages.zip")
                val destZip = File(Environment.getExternalStorageDirectory().absolutePath + "/MyFiles/")
                CopyFromAssets.unzip(zip, destZip)
                zip.delete()

                Toast.makeText(this, "Languages imported!", Toast.LENGTH_SHORT).show()

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

            if (checkAvailability()) {
                this.startActivity(intentMain)
            } else {
                Toast.makeText(this, "Needed languages not imported", Toast.LENGTH_SHORT).show()
            }
        }

        val prev = findViewById<ImageButton>(R.id.PrevFromLanguages)
        prev.setOnClickListener{
            val intentMain = Intent(
                this,
                MainActivity::class.java
            )
            this.startActivity(intentMain)
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
            } else {
                checkAndRequestPermissions()
            }
            permissions.initDir(this)
        }
    }
}