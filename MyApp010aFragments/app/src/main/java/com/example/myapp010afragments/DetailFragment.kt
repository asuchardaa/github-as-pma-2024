package com.example.myapp010afragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class DetailFragment : Fragment() {

    private lateinit var textViewTitle: TextView
    private lateinit var textViewAuthor: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        textViewTitle = view.findViewById(R.id.textViewTitle)
        textViewAuthor = view.findViewById(R.id.textViewAuthor)

        arguments?.let {
            val title = it.getString("title")
            val author = it.getString("author")
            updateDetails(title ?: "Unknown", author ?: "Unknown")
        }

        return view
    }

    fun updateDetails(title: String, author: String) {
        textViewTitle.text = title
        textViewAuthor.text = author
    }
}