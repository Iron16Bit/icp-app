package com.icp.icp_app

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment


class PopUpFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pop_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val text = view.findViewById<TextView>(R.id.text)
        val textString = "In order to use the app, please give the requested permissions.\n" +
                "\n" +
                "SELECT SLIDES: select from local storage which set of ICP slides to show. Then press import on the right\n" +
                "\n" +
                "SELECT LANGUAGE: select from local storage the support for the programming languages used in the slides. Then press import on the right\n" +
                "\n" +
                "SHOW SLIDES: open slides in browser\n"
        text.text = textString
        text.movementMethod = ScrollingMovementMethod()

        val exit = view.findViewById<Button>(R.id.exit)
        exit.setOnClickListener{
            dismiss()
        }
    }
}