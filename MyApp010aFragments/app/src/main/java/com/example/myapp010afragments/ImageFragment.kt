package com.example.myapp010afragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class ImageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_image, container, false)
        val imageView = view.findViewById<ImageView>(R.id.imageView)

        // Nastavíme nějaký obrázek, např. z drawable složky
        imageView.setImageResource(R.drawable.sample_image)

        return view
    }
}
