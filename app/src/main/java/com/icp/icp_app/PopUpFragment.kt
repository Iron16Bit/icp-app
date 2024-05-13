package com.icp.icp_app

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment

// The info pop-up
class PopUpFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pop_up, container, false)
    }

    private var sharedPref: SharedPreferences? = null
    private fun updateAllColors() {
        if (sharedPref?.contains("actual_theme") == true) {
            val theme = sharedPref!!.getString("actual_theme", null)

            view?.findViewById<RelativeLayout>(R.id.PBackground)?.setBackgroundColor(
                Color.parseColor(
                    if (theme == "dark") {
                        "#364c6e"
                    } else {
                        "#ffffff"
                    }
                )
            )

            view?.findViewById<TextView>(R.id.PsubTitle1)?.setTextColor(
                Color.parseColor(
                    if (theme == "dark") {
                        "#ffffff"
                    } else {
                        "#000000"
                    }
                )
            )

            view?.findViewById<RelativeLayout>(R.id.PBgContainer1)?.setBackgroundColor(
                Color.parseColor(
                    if (theme == "dark") {
                        "#26364E"
                    } else {
                        "#d8cedb"
                    }
                )
            )

            view?.findViewById<TextView>(R.id.PText1)?.setTextColor(
                Color.parseColor(
                    if (theme == "dark") {
                        "#ffffff"
                    } else {
                        "#000000"
                    }
                )
            )

            view?.findViewById<TextView>(R.id.PsubTitle2)?.setTextColor(
                Color.parseColor(
                    if (theme == "dark") {
                        "#ffffff"
                    } else {
                        "#000000"
                    }
                )
            )

            view?.findViewById<RelativeLayout>(R.id.PBgContainer2)?.setBackgroundColor(
                Color.parseColor(
                    if (theme == "dark") {
                        "#26364E"
                    } else {
                        "#d8cedb"
                    }
                )
            )

            view?.findViewById<TextView>(R.id.PText2)?.setTextColor(
                Color.parseColor(
                    if (theme == "dark") {
                        "#ffffff"
                    } else {
                        "#000000"
                    }
                )
            )

            view?.findViewById<TextView>(R.id.PsubTitle3)?.setTextColor(
                Color.parseColor(
                    if (theme == "dark") {
                        "#ffffff"
                    } else {
                        "#000000"
                    }
                )
            )

            view?.findViewById<RelativeLayout>(R.id.PBgContainer3)?.setBackgroundColor(
                Color.parseColor(
                    if (theme == "dark") {
                        "#26364E"
                    } else {
                        "#d8cedb"
                    }
                )
            )

            view?.findViewById<TextView>(R.id.PText3)?.setTextColor(
                Color.parseColor(
                    if (theme == "dark") {
                        "#ffffff"
                    } else {
                        "#000000"
                    }
                )
            )

            view?.findViewById<RelativeLayout>(R.id.PBgContainer4)?.setBackgroundColor(
                Color.parseColor(
                    if (theme == "dark") {
                        "#26364E"
                    } else {
                        "#d8cedb"
                    }
                )
            )

            view?.findViewById<TextView>(R.id.PText4)?.setTextColor(
                Color.parseColor(
                    if (theme == "dark") {
                        "#ffffff"
                    } else {
                        "#000000"
                    }
                )
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedPref = requireActivity().getSharedPreferences("my_pref", AppCompatActivity.MODE_PRIVATE)
        super.onViewCreated(view, savedInstanceState)
        updateAllColors()

        val text1 = view.findViewById<TextView>(R.id.PText1)
        val textString1 = "Select the set of ICP slides to show from local storage. Then press import"
        text1.text = textString1

        val text2 = view.findViewById<TextView>(R.id.PText2)
        val textString2 = "Select from local storage the support for the programming languages used in the slides. Then press import\n" +
                "\n" +
                "NOTE: Languages are distributed through .zip files"
        text2.text = textString2

        val text3 = view.findViewById<TextView>(R.id.PText3)
        val textString3 = "First start the local server, then either:\n" +
                "- Open them on the default browser\n" +
                "- Copy the link and paste it in the desired browser"
        text3.text = textString3

        val text4 = view.findViewById<TextView>(R.id.PText4)
        val textString4 = "Java, Python and C++ can only be used in Firefox"
        text4.text = textString4

        val exit = view.findViewById<Button>(R.id.exit)
        exit.setOnClickListener{
            dismiss()
        }

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            exit.callOnClick()
        }
    }
}