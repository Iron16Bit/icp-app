package com.icp.icp_app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Tutorial : AppCompatActivity() {

    private var sharedPref: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = this.getSharedPreferences("my_pref", MODE_PRIVATE)
        setContentView(R.layout.activity_tutorial)

        if (sharedPref?.contains("show_tutorial") == false) {
            val editor = sharedPref?.edit()
            if (editor != null) {
                editor.putString("show_tutorial", "true")
                editor.apply()
            }
        } else {
            val tutorial = sharedPref!!.getString("show_tutorial", null)
            if (tutorial == "false") {
                val intentMain = Intent(
                    this,
                    ImportSlides::class.java
                )
                this.startActivity(intentMain)
            }
        }

        val exit = findViewById<Button>(R.id.exit)
        exit.setOnClickListener{
            if (sharedPref?.contains("show_tutorial") == true) {
                val editor = sharedPref?.edit()
                if (editor != null) {
                    editor.putString("show_tutorial", "false")
                    editor.apply()
                }
            }
            val intentMain = Intent(
                this,
                ImportSlides::class.java
            )
            this.startActivity(intentMain)
        }

        val callback = onBackPressedDispatcher.addCallback(this) {
            exit.callOnClick()
        }
    }
}