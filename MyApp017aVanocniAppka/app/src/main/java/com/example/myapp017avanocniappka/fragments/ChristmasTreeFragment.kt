package com.example.myapp017avanocniappka.fragments

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.myapp017avanocniappka.R

class ChristmasTreeFragment : Fragment(R.layout.fragment_tree) {

    private var treeSize: Int = 5

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tree, container, false)

        val sizeInput = view.findViewById<EditText>(R.id.treeSizeInput)
        val drawButton = view.findViewById<Button>(R.id.drawButton)
        val treeTextView = view.findViewById<TextView>(R.id.treeTextView)

        drawButton.setOnClickListener {
            try {
                treeSize = sizeInput.text.toString().toInt()

                if (treeSize < 3) treeSize = 3
                if (treeSize > 20) treeSize = 18

                treeTextView.text = drawTreeWithAnimation(treeSize)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Zadejte platnou velikost!", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun drawTreeWithAnimation(size: Int): String {
        val treeBuilder = StringBuilder()

        for (i in 0 until size) {
            val stars = "*".repeat(2 * i + 1)

            val decoratedStars = stars.map { char ->
                if (char == '*') {
                    if (Math.random() > 0.7) {
                        "o"
                    } else {
                        "*"
                    }
                } else {
                    char.toString()
                }
            }.joinToString("")

            treeBuilder.append(decoratedStars).append("\n")
        }

        val trunkWidth = 3
        val trunkHeight = 2
        val trunkSpaces = " ".repeat(size - trunkWidth / 2)
        for (i in 0 until trunkHeight) {
            treeBuilder.append(trunkSpaces).append("|".repeat(trunkWidth)).append("\n")
        }

        animateTreeFadeIn()
        startSparkleEffect()

        return treeBuilder.toString()
    }

    private fun animateTreeFadeIn() {
        val treeTextView = view?.findViewById<TextView>(R.id.treeTextView)
        val fadeIn = ObjectAnimator.ofFloat(treeTextView, "alpha", 0f, 1f)
        fadeIn.duration = 2000
        fadeIn.start()
    }

    private fun startSparkleEffect() {
        val handler = Handler(Looper.getMainLooper())

        val runnable = object : Runnable {
            override fun run() {
                val treeTextView = view?.findViewById<TextView>(R.id.treeTextView)
                val currentText = treeTextView?.text.toString()

                val modifiedText = currentText.replaceFirst("*", if (Math.random() > 0.5) "o" else "*")
                treeTextView?.text = modifiedText

                handler.postDelayed(this, 1000)
            }
        }

        handler.postDelayed(runnable, 1000)
    }
}
