package com.example.myapp005objednavka

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.myapp005objednavka.databinding.MainActivityBinding
import com.example.myapp005objednavka.ui.theme.MyApp005ObjednavkaTheme

class MainActivity : ComponentActivity() {

    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setContent {
            MyApp005ObjednavkaTheme {
                OrderSystemScreen()
            }
        }

        binding.btnOrder.setOnClickListener {
            binding.textSummary.text = "Objednávka odeslána přes ViewBinding!"
        }
    }
}
