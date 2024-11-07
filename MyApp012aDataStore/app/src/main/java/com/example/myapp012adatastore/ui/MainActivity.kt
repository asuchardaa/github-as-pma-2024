package com.example.myapp012adatastore.ui

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapp012adatastore.R
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private val NAME_KEY = stringPreferencesKey("name")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editTextName = findViewById<EditText>(R.id.editTextName)
        val buttonSave = findViewById<Button>(R.id.buttonSave)
        val textViewDisplay = findViewById<TextView>(R.id.textViewDisplay)

        val savedName = runBlocking { getName() }
        textViewDisplay.text = "Stored Name: $savedName"

        buttonSave.setOnClickListener {
            val name = editTextName.text.toString()
            saveName(name)
            textViewDisplay.text = "Stored Name: $name"
        }
    }

    private fun saveName(name: String) {
        runBlocking {
            dataStore.edit { preferences ->
                preferences[NAME_KEY] = name
            }
        }
    }

    private suspend fun getName(): String {
        return dataStore.data.map { preferences ->
            preferences[NAME_KEY] ?: "No name saved"
        }.first()
    }
}
