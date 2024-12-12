package com.example.myapp017avanocniappka.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapp017avanocniappka.R
import com.example.myapp017avanocniappka.fragments.ChristmasTreeFragment
import com.example.myapp017avanocniappka.fragments.GreetingFragment
import com.example.myapp017avanocniappka.fragments.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // Nastavení defaultního fragmentu
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, GreetingFragment())
                .commit()
        }

        // Nastavení posluchače pro BottomNavigation
        bottomNav.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment = when (item.itemId) {
                R.id.nav_greeting -> GreetingFragment() // Vánoční přání
                R.id.nav_tree -> ChristmasTreeFragment() // Vykreslení stromečku
                R.id.nav_settings -> SettingsFragment() // Nastavení
                else -> GreetingFragment()
            }
            // Nahrazení fragmentu na základě výběru
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, selectedFragment)
                .commit()
            true
        }
    }
}
