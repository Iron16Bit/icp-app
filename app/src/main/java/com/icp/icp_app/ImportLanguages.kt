package com.icp.icp_app

import android.content.Intent
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
import java.io.File

// The second page of the app, used to import languages from local storage

class ImportLanguages : AppCompatActivity() {

    // Strings containing, for each language, language+\n
    var availableLang = "";
    var neededLang = "";

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

    // Updates the TextView with all the currently available languages
    private fun updateAvailableLanguages() {
        availableLang = "";
        val folder = File(Environment.getExternalStorageDirectory().absolutePath + "/MyFiles/")

        val availableFiles = folder.list()
        for (f in availableFiles!!) {
            if (f.endsWith(".js") && !f.startsWith("reveal")) {
                val tmp = f.split(".")
                availableLang += tmp[0] + "\n"
            }
        }
    }

    // Allows to choose a language from local storage
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

    // The import button goes from grey to colored
    private fun updateColor(btn: Button?) {
        if (Language != null) {
            btn?.background?.setTint(Color.parseColor("#604D9B"))
        }
    }

    // Scans the index.html for all the languages needed to make the slides work
    private fun getLanguagesInSlides() {
        updateAvailableLanguages()
        neededLang = ""
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
                    val available = checkAvailability(name);
                    if (available) {
                        text += "- $name.iife.js  ✅\n"
                    } else {
                        text += "- $name.iife.js  ❌\n"
                    }
                    neededLang += name + "\n"
                }
            }
        } else {
            text += "(None)\n"
        }

        val textView = findViewById<TextView>(R.id.NeededLanguages)
        textView.text = text
    }


    // Checks if a single specific langauge is available
    private fun checkAvailability(lang: String): Boolean {
        val allAvailableLanguages = availableLang.split("\n")
        for (l in allAvailableLanguages) {
            if (lang == l) {
                return true;
            }
        }
        return false;
    }

    // Checks if all the needed languages are available
    private fun checkFullAvailability(): Boolean {
        val allNeededLanguages = neededLang.split("\n")
        for (l in allNeededLanguages) {
            val availability = checkAvailability(l)
            if (!availability) {
                return false
            }
        }
        return true
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

                val path = Language!!.split("/")
                val fileName = path[path.lastIndex]
                // Since languages are distributed through .zip files, at least checks if the selected file is a zip
                val regex = ".*.zip".toRegex()

                if (fileName.contains(regex)) {
                    copy.copyExternal(Language!!, dstString, "export_languages.zip")
                    val zip =
                        File(Environment.getExternalStorageDirectory().absolutePath + "/MyFiles/export_languages.zip")
                    val destZip =
                        File(Environment.getExternalStorageDirectory().absolutePath + "/MyFiles/")
                    CopyFromAssets.unzip(zip, destZip)
                    zip.delete()

                    Toast.makeText(this, "Languages imported!", Toast.LENGTH_SHORT).show()

                    updateAvailableLanguages()
                    getLanguagesInSlides()
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
                ShowSlides::class.java
            )

            // Doesn't allow to move to the next page if the needed languages haven't been imported
            if (checkFullAvailability()) {
                this.startActivity(intentMain)
            } else {
                Toast.makeText(this, "Needed languages not imported", Toast.LENGTH_SHORT).show()
            }
        }

        val prev = findViewById<ImageButton>(R.id.PrevFromLanguages)
        prev.setOnClickListener{
            val intentMain = Intent(
                this,
                ImportSlides::class.java
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