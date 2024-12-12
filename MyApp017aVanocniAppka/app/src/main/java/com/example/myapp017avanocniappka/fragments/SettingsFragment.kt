package com.example.myapp017avanocniappka.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.myapp017avanocniappka.R

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val themeSpinner = view.findViewById<Spinner>(R.id.themeSpinner)
        val languageSpinner = view.findViewById<Spinner>(R.id.languageSpinner)

        return view
    }
}
