package com.example.myapp005objednavka

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = (option == selectedCard),
                        onClick = { selectedCard = option }
                    )
                    Text(option, modifier = Modifier.padding(start = 8.dp))
                }
            }

            CheckboxWithLabel(
                label = "Extra chlazení",
                checked = extraCooling,
                onCheckedChange = { extraCooling = it }
            )
            CheckboxWithLabel(
                label = "Přetaktování",
                checked = overclocking,
                onCheckedChange = { overclocking = it }
            )
            CheckboxWithLabel(
                label = "RGB ledky",
                checked = rgbLighting,
                onCheckedChange = { rgbLighting = it }
            )

            Button(onClick = {
                orderSummary = "Objednaná karta: $selectedCard\n" +
                        "Extra chlazení: ${if (extraCooling) "Ano" else "Ne"}\n" +
                        "Přetaktování: ${if (overclocking) "Ano" else "Ne"}\n" +
                        "RGB ledky: ${if (rgbLighting) "Ano" else "Ne"}"
            }) {
                Text("Objednávka")
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
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    )  {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(label, modifier = Modifier.padding(start = 8.dp))
    }
}
