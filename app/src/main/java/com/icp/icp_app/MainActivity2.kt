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

                val fileName = Language!!.split("/")

                if (fileName[fileName.lastIndex] == "export_languages.zip") {
                    copy.copyExternal(Language!!, dstString, "export_languages.zip")
                    val zip =
                        File(Environment.getExternalStorageDirectory().absolutePath + "/MyFiles/export_languages.zip")
                    val destZip =
                        File(Environment.getExternalStorageDirectory().absolutePath + "/MyFiles/")
                    CopyFromAssets.unzip(zip, destZip)
                    zip.delete()

                    Toast.makeText(this, "Languages imported!", Toast.LENGTH_SHORT).show()

                    updateAvailableLanguages()
                } else {
                    Toast.makeText(this, "Selected file not supported", Toast.LENGTH_SHORT).show()
                }
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