package com.example.myapp011asharedpreferences

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private val PREFS_NAME = "MyPrefs"
    private val KEY_NAME = "name"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editTextName = findViewById<EditText>(R.id.editTextName)
        val buttonSave = findViewById<Button>(R.id.buttonSave)
        val textViewDisplay = findViewById<TextView>(R.id.textViewDisplay)

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val savedName = sharedPreferences.getString(KEY_NAME, "No name saved")
        textViewDisplay.text = "Stored Name: $savedName"

        buttonSave.setOnClickListener {
            val name = editTextName.text.toString()
            saveName(name)
            textViewDisplay.text = "Stored Name: $name"
        }
    }

    private fun saveName(name: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_NAME, name)
        editor.apply()
    }
}
