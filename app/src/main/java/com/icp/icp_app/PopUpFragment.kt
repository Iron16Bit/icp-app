package com.icp.icp_app

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
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

        val text1 = view.findViewById<TextView>(R.id.Text1)
        val textString1 = "Select the set of ICP slides to show from local storage. Then press import"
        text1.text = textString1

        val text2 = view.findViewById<TextView>(R.id.Text2)
        val textString2 = "Select from local storage the support for the programming languages used in the slides. Then press import on the right\n" +
                "\n" +
                "NOTE: Languages are distributed through .zip files"
        text2.text = textString2

        val text3 = view.findViewById<TextView>(R.id.Text3)
        val textString3 = "First start the local server, then either:\n" +
                "- Open them on the default browser\n" +
                "- Copy the link and paste it in the desired browser"
        text3.text = textString3

        val text4 = view.findViewById<TextView>(R.id.Text4)
        val textString4 = "Java, Python and C++ can only be used in Firefox"
        text4.text = textString4

        val exit = view.findViewById<Button>(R.id.exit)
        exit.setOnClickListener{
            dismiss()
        }
    }
}