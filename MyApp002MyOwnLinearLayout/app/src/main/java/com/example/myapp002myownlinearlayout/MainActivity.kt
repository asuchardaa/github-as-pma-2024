package com.example.myapp002myownlinearlayout

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    private lateinit var displayTextView: TextView
    private var currentNumber: String = ""
    private var operator: String? = null
    private var firstNumber: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        displayTextView = findViewById(R.id.displayTextView)

        val buttons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
            R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide,
            R.id.btnEquals, R.id.btnClear
        )

        buttons.forEach { id ->
            findViewById<Button>(id).setOnClickListener { onButtonClick(id) }
        }
    }

    private fun onButtonClick(id: Int) {
        when (id) {
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9 -> {
                currentNumber += findViewById<Button>(id).text
                displayTextView.text = currentNumber
            }
            R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide -> {
                operator = findViewById<Button>(id).text.toString()
                firstNumber = currentNumber.toDouble()
                currentNumber = ""
                displayTextView.text = "$firstNumber $operator"
            }
            R.id.btnEquals -> {
                val secondNumber = currentNumber.toDouble()
                val result = when (operator) {
                    "+" -> firstNumber + secondNumber
                    "-" -> firstNumber - secondNumber
                    "*" -> firstNumber * secondNumber
                    "/" -> firstNumber / secondNumber
                    else -> 0.0
                }
                displayTextView.text = result.toString()
                currentNumber = result.toString()
                operator = null
            }
            R.id.btnClear -> {
                currentNumber = ""
                operator = null
                firstNumber = 0.0
                displayTextView.text = "0"
            }
        }
    }
}
