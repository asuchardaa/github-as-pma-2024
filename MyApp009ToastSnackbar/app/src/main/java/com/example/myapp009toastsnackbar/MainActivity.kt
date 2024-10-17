package com.example.myapp009toastsnackbar

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp009toastsnackbar.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializace ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nastavení akce pro Toast
        binding.btnShowToast.setOnClickListener {
            showCustomToast("Je čas na sklenici vody!")
        }

        // Nastavení akce pro Snackbar
        binding.btnShowSnackbar.setOnClickListener {
            showSnackbar()
        }
    }

    private fun showCustomToast(message: String) {
        val inflater: LayoutInflater = layoutInflater
        val layout = inflater.inflate(
            R.layout.custom_toast_layout,
            findViewById(R.id.custom_toast_container)
        )

        val toastIcon: ImageView = layout.findViewById(R.id.toastIcon)
        val toastText: TextView = layout.findViewById(R.id.toastText)
        toastIcon.setImageResource(R.drawable.ic_toast_icon)
        toastText.text = message

        with(Toast(applicationContext)) {
            duration = Toast.LENGTH_SHORT
            view = layout
            setGravity(Gravity.CENTER_VERTICAL, 0, 200)
            show()
        }
    }

    private fun showSnackbar() {
        val snackbar = Snackbar.make(binding.main, "Je čas se napít vody", Snackbar.LENGTH_LONG)
        snackbar.setTextColor(Color.WHITE)
        snackbar.setBackgroundTint(Color.BLACK)
        snackbar.setActionTextColor(Color.YELLOW)
        snackbar.setAction("ODLOŽIT") {
            showCustomToast("Připomínka byla odložena o 10 minut")
        }
        snackbar.show()
    }
}
