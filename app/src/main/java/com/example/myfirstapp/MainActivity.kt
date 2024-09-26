package com.example.myfirstapp

import android.content.DialogInterface
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.Button
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var resultTextView: TextView
    private lateinit var genderGroup: RadioGroup
    private lateinit var satisfactionSlider: SeekBar
    private lateinit var satisfactionLevelText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firstNameEditText = findViewById(R.id.firstName)
        lastNameEditText = findViewById(R.id.lastName)
        cityEditText = findViewById(R.id.city)
        ageEditText = findViewById(R.id.age)
        genderGroup = findViewById(R.id.genderGroup)
        satisfactionSlider = findViewById(R.id.satisfactionSlider)

        val submitButton = findViewById<Button>(R.id.submitButton)
        val clearButton = findViewById<Button>(R.id.clearButton)
        resultTextView = findViewById(R.id.resultTextView)
        val btnCzech: Button = findViewById(R.id.btnCzech)
        val btnEnglish: Button = findViewById(R.id.btnEnglish)

        submitButton.setOnClickListener {
            if (validateInputs()) {
                val firstName = firstNameEditText.text.toString()
                val lastName = lastNameEditText.text.toString()
                val city = cityEditText.text.toString()
                val age = ageEditText.text.toString()
                val selectedGenderId = genderGroup.checkedRadioButtonId
                val gender = when (selectedGenderId) {
                    R.id.radioMale -> "Muž"
                    R.id.radioFemale -> "Žena"
                    else -> "Nezvoleno"
                }
                val satisfactionLevel = satisfactionSlider.progress

                resultTextView.text = getString(R.string.result_text,
                    firstName, lastName, city, age, gender, satisfactionLevel)
            }
        }


        clearButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Opravdu chcete vymazat všechny údaje?")
                .setPositiveButton("Ano") { _, _ ->
                    clearFields()
                }
                .setNegativeButton("Ne", null)
            builder.create().show()
        }

        btnCzech.setOnClickListener {
            setLocale("cs")
        }

        btnEnglish.setOnClickListener {
            setLocale("en")
        }
    }

    private fun validateInputs(): Boolean {
        val firstName = firstNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()
        val city = cityEditText.text.toString().trim()
        val age = ageEditText.text.toString().trim()

        return when {
            firstName.isEmpty() -> {
                Toast.makeText(this, "Zadejte jméno", Toast.LENGTH_SHORT).show()
                false
            }
            lastName.isEmpty() -> {
                Toast.makeText(this, "Zadejte příjmení", Toast.LENGTH_SHORT).show()
                false
            }
            city.isEmpty() -> {
                Toast.makeText(this, "Zadejte obec", Toast.LENGTH_SHORT).show()
                false
            }
            age.isEmpty() -> {
                Toast.makeText(this, "Zadejte věk", Toast.LENGTH_SHORT).show()
                false
            }
            !age.isDigitsOnly() || age.toInt() <= 0 -> {
                Toast.makeText(this, "Zadejte platný věk", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun clearFields() {
        firstNameEditText.text.clear()
        lastNameEditText.text.clear()
        cityEditText.text.clear()
        ageEditText.text.clear()
        genderGroup.clearCheck()
        satisfactionSlider.progress = 0
        resultTextView.text = " "
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        recreate()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("result", resultTextView.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        resultTextView.text = savedInstanceState.getString("result")
    }
}
