package com.example.myapp005objednavka

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
                // Main layout
                OrderSystem()
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OrderSystem() {
    var selectedCard by remember { mutableStateOf("") }
    var extraCooling by remember { mutableStateOf(false) }
    var overclocking by remember { mutableStateOf(false) }
    var rgbLighting by remember { mutableStateOf(false) }
    var orderSummary by remember { mutableStateOf("") }

    val imageRes = when (selectedCard) {
        "AMD 480x" -> painterResource(id = R.drawable.amd_480x_card)
        "GTX 950" -> painterResource(id = R.drawable.gtx_950_card)
        "GTX 1050" -> painterResource(id = R.drawable.gtx_1050_card)
        else -> null // No image to display
    }

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Column(modifier = Modifier.fillMaxSize()) {
            Text(text = "Select Graphics Card:", style = MaterialTheme.typography.headlineSmall)

            val radioOptions = listOf("AMD 480x", "GTX 950", "GTX 1050")
            Column {
                radioOptions.forEach { text ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (text == selectedCard),
                                onClick = { selectedCard = text }
                            )
                            .padding(vertical = 8.dp)
                    ) {
                        RadioButton(
                            selected = (text == selectedCard),
                            onClick = { selectedCard = text },
                            modifier = Modifier.padding(12.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = text, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            CheckboxWithLabel(checked = extraCooling, onCheckedChange = { extraCooling = it }, label = "Extra Cooling")
            CheckboxWithLabel(checked = overclocking, onCheckedChange = { overclocking = it }, label = "Overclocking")
            CheckboxWithLabel(checked = rgbLighting, onCheckedChange = { rgbLighting = it }, label = "RGB Lighting")

            Spacer(modifier = Modifier.height(16.dp))

            // Button to submit the order
            Button(
                onClick = {
                    orderSummary = "Selected Card: $selectedCard\n" +
                            "Extra Cooling: ${if (extraCooling) "Yes" else "No"}\n" +
                            "Overclocking: ${if (overclocking) "Yes" else "No"}\n" +
                            "RGB Lighting: ${if (rgbLighting) "Yes" else "No"}"
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Order")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Text for order summary
            Text(text = "Order Summary:", style = MaterialTheme.typography.headlineSmall)
            BasicText(text = orderSummary)

            // Display the image below the order summary
            Spacer(modifier = Modifier.height(16.dp))
            if (imageRes != null) {
                Image(painter = imageRes, contentDescription = selectedCard, modifier = Modifier.size(200.dp))
            }
        }
    }
}



@Composable
fun CheckboxWithLabel(checked: Boolean, onCheckedChange: (Boolean) -> Unit, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview(showBackground = true)
@Composable
fun OrderSystemPreview() {
    MyApp005ObjednavkaTheme {
        OrderSystem()
    }
}
