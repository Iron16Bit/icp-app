package com.icp.icp_app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class MainActivity3 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_slides)

        val server = Server()
        val startServer = findViewById<Button>(R.id.SHOW)
        startServer.setOnClickListener {
            server.launch()
            val url = "http://localhost:8080"
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(url)
            )
            val chooseIntent = Intent.createChooser(intent, "Choose from below")
            startActivity(chooseIntent)
        }

        val prev = findViewById<ImageButton>(R.id.PrevFromShow)
        prev.setOnClickListener{
            val intentMain = Intent(
                this,
                MainActivity2::class.java
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