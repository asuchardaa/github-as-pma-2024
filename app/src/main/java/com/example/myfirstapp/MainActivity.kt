package com.example.myfirstapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val firstNameEditText = findViewById<EditText>(R.id.firstName)
        val lastNameEditText = findViewById<EditText>(R.id.lastName)
        val cityEditText = findViewById<EditText>(R.id.city)
        val ageEditText = findViewById<EditText>(R.id.age)
        val submitButton = findViewById<Button>(R.id.submitButton)
        val clearButton = findViewById<Button>(R.id.clearButton)
        val resultTextView = findViewById<TextView>(R.id.resultTextView)

        // tlačítko Odeslat
        submitButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()
            val city = cityEditText.text.toString()
            val age = ageEditText.text.toString()

            resultTextView.text = "Jméno: $firstName $lastName\nObec: $city\nVěk: $age"
        }

        // tlačítko Vymazat
        clearButton.setOnClickListener {
            firstNameEditText.text.clear()
            lastNameEditText.text.clear()
            cityEditText.text.clear()
            ageEditText.text.clear()
            resultTextView.text = "text"
        }
    }
}