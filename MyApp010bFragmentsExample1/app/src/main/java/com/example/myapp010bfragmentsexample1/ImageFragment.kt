package com.example.myapp010bfragmentsexample1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapp010bfragmentsexample1.databinding.FragmentImageBinding

class ImageFragment : Fragment() {

    private lateinit var binding: FragmentImageBinding
    private var isFirstImage = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.changeImageButton.setOnClickListener {
            if (isFirstImage) {
                binding.imageView.setImageResource(R.drawable.cat_image2)
            } else {
                binding.imageView.setImageResource(R.drawable.cat_image)
            }
            isFirstImage = !isFirstImage
        }
    }
}
