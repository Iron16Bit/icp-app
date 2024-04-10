package com.icp.icp_app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MainActivity2 : AppCompatActivity() {
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
            this.startActivity(intentMain)
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
    }
}