package com.example.myapp005objednavka

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapp005objednavka.ui.theme.MyApp005ObjednavkaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApp005ObjednavkaTheme {
                OrderSystemScreen()
            }
        }
    }
}

@Composable
fun OrderSystemScreen() {
    var selectedCard by remember { mutableStateOf("") }
    var extraCooling by remember { mutableStateOf(false) }
    var overclocking by remember { mutableStateOf(false) }
    var rgbLighting by remember { mutableStateOf(false) }
    var orderSummary by remember { mutableStateOf("") }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Vyber si grafickou kartu:", style = MaterialTheme.typography.headlineSmall)

            val radioOptions = listOf("AMD 480x", "GTX 950", "GTX 1050")
            radioOptions.forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = (option == selectedCard),
                        onClick = { selectedCard = option }
                    )
                    Text(option, modifier = Modifier.padding(start = 8.dp))
                }
            }

            CheckboxWithLabel(
                label = "Extra Cooling",
                checked = extraCooling,
                onCheckedChange = { extraCooling = it }
            )
            CheckboxWithLabel(
                label = "Overclocking",
                checked = overclocking,
                onCheckedChange = { overclocking = it }
            )
            CheckboxWithLabel(
                label = "RGB Lighting",
                checked = rgbLighting,
                onCheckedChange = { rgbLighting = it }
            )

            Button(onClick = {
                orderSummary = "Selected Card: $selectedCard\n" +
                        "Extra Cooling: ${if (extraCooling) "Yes" else "No"}\n" +
                        "Overclocking: ${if (overclocking) "Yes" else "No"}\n" +
                        "RGB Lighting: ${if (rgbLighting) "Yes" else "No"}"
            }) {
                Text("Order")
            }

            Text(orderSummary, modifier = Modifier.padding(top = 16.dp))

            val imageResource = when (selectedCard) {
                "AMD 480x" -> R.drawable.amd_480x_card
                "GTX 950" -> R.drawable.gtx_950_card
                "GTX 1050" -> R.drawable.gtx_1050_card
                else -> 0
            }
            if (imageResource != 0) {
                Image(
                    painter = painterResource(id = imageResource),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
            }
        }
    }
}

@Composable
fun CheckboxWithLabel(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(label)
    }
}

@Preview(showBackground = true)
@Composable
fun OrderSystemPreview() {
    MyApp005ObjednavkaTheme {
        OrderSystemScreen()
    }
}
