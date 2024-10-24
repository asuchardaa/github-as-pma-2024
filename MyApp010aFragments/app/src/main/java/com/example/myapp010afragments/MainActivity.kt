package com.example.myapp010afragments

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonBooks = findViewById<Button>(R.id.button_books)
        val buttonImages = findViewById<Button>(R.id.button_images)

        // Výchozí stav: zobrazení seznamu knih
        if (savedInstanceState == null) {
            replaceFragment(ListFragment())
        }

        // Přepnutí na seznam knih
        buttonBooks.setOnClickListener {
            replaceFragment(ListFragment())
        }

        // Přepnutí na zobrazení obrázků
        buttonImages.setOnClickListener {
            replaceFragment(ImageFragment())
        }
    }

    // Metoda pro přepínání fragmentů
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Tato metoda bude volána z ListFragmentu při výběru knihy
    fun onBookSelected(title: String, author: String) {
        val detailFragment = DetailFragment()

        // Předáme detaily knihy do DetailFragmentu
        val bundle = Bundle().apply {
            putString("title", title)
            putString("author", author)
        }
        detailFragment.arguments = bundle

        // Nahradíme stávající fragment (ListFragment) DetailFragmentem
        replaceFragment(detailFragment)
    }
}
