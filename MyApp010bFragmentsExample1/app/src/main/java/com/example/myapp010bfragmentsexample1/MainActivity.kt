package com.example.myapp010bfragmentsexample1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapp010bfragmentsexample1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Načtení výchozího fragmentu při spuštění aplikace
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ImageFragment())
                .commit()
        }

        // Přepínání mezi fragmenty
        binding.btnImageFragment.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ImageFragment())
                .commit()
        }

        binding.btnTextFragment.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TextFragment())
                .commit()
        }

        binding.btnButtonFragment.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ButtonFragment())
                .commit()
        }
    }
}
