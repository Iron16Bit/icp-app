package com.icp.icp_app

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

// The second page of the app, used to import languages from local storage

class ImportLanguages : AppCompatActivity() {

    // Strings containing, for each language, language+\n
    var availableLang = "";
    var neededLang = "";

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

            // We have to scan for all the imports of some sources in the html file, and from them extract only the name of those that import supports for languages
            val regex = "<script src=.*></script>".toRegex()
            val scripts = regex.findAll(htmlContent)

            for (s in scripts) {
                val split1 = s.value.split("src=")
                val split2 = split1[1].split(">")
                val split3 = split2[0].split(".")
                val name = split3[0].drop(1)

                if (name != "reveal" && !name.contains("cdn")) {
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

        if (text.isEmpty()) {
            text += "(None)\n"
        }

        val textView = findViewById<TextView>(R.id.NeededLanguages)
        textView.text = text
    }


    // Checks if a single specific language is available
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

    private var sharedPref: SharedPreferences? = null

    private fun updateAllColors() {
        if (sharedPref?.contains("actual_theme") == true) {
            val theme = sharedPref!!.getString("actual_theme", null)

            val background = findViewById<RelativeLayout>(R.id.Background)
            background.setBackgroundColor(Color.parseColor(if (theme == "dark") { "#26364E" } else { "#ffffff" }))

            val titleLanguages = findViewById<TextView>(R.id.TitleLanguages)
            titleLanguages.setTextColor(Color.parseColor(if (theme == "dark") { "#ffffff" } else { "#000000" }))

            val containerBg = findViewById<RelativeLayout>(R.id.ContainerBackground)
            containerBg.setBackgroundColor(Color.parseColor(if (theme == "dark") { "#182436" } else { "#d8cedb" }))

            val title1 = findViewById<TextView>(R.id.NeededLanguagesTitle)
            title1.setTextColor(Color.parseColor(if (theme == "dark") { "#d6b9fa" } else { "#a15afa" }))

            val text1 = findViewById<TextView>(R.id.NeededLanguages)
            text1.setTextColor(Color.parseColor(if (theme == "dark") { "#ffffff" } else { "#000000" }))

            val containerBg1 = findViewById<RelativeLayout>(R.id.ContainerBackground1)
            containerBg1.setBackgroundColor(Color.parseColor(if (theme == "dark") { "#182436" } else { "#d8cedb" }))

            val title2 = findViewById<TextView>(R.id.importTitle)
            title2.setTextColor(Color.parseColor(if (theme == "dark") { "#bbb9fa" } else { "#5c57ff" }))

            val text2 = findViewById<TextView>(R.id.importInfo)
            text2.setTextColor(Color.parseColor(if (theme == "dark") { "#ffffff" } else { "#000000" }))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = this.getSharedPreferences("my_pref", MODE_PRIVATE)
        setContentView(R.layout.activity_languages)
        updateAllColors()

        getLanguagesInSlides()
        updateAvailableLanguages()

        val selectLanguage = findViewById<Button>(R.id.SelectLanguage)
        selectLanguage.setOnClickListener {
            getContentLanguage.launch("*/*")
        }

        val importLanguage = findViewById<Button>(R.id.ImportLanguage)
        importLanguage.setOnClickListener {

            Thread(Runnable {
                val loading = findViewById<ProgressBar>(R.id.loading_progress_xml)
                runOnUiThread { loading.isVisible = true
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);}
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

                        runOnUiThread { Toast.makeText(this, "Languages imported!", Toast.LENGTH_SHORT).show() }

                        updateAvailableLanguages()
                        runOnUiThread { getLanguagesInSlides() }
                    } else {
                        runOnUiThread { Toast.makeText(this, "Selected file not supported", Toast.LENGTH_SHORT).show() }
                    }
                } else {
                    runOnUiThread { Toast.makeText(this, "No language has been selected", Toast.LENGTH_SHORT).show() }
                }
                runOnUiThread { loading.isVisible = false
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)}
            }).start()
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

        val settingsButton = findViewById<ImageButton>(R.id.Permissions)
        settingsButton.setOnClickListener {
            val intentMain = Intent(
                this,
                com.icp.icp_app.Settings::class.java
            )
            this.startActivity(intentMain)
        }

        val callback = onBackPressedDispatcher.addCallback(this) {
            prev.callOnClick()
        }
    }

    fun import() {

    }
}