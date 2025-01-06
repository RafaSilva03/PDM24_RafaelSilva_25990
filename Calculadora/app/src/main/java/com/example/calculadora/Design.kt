package com.example.calculadora

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculadora.ui.theme.CalculadoraTheme

@Composable
fun CalculatorUI() {
    // Retrieve state from rememberCalculatorState composable
    val state = rememberCalculatorState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Display
        BasicTextField(
            value = state.displayText,
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(16.dp),
            textStyle = LocalTextStyle.current.copy(
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ),
            singleLine = true,
            enabled = false
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Layout of Buttons
        val buttons = listOf(
            listOf("7", "8", "9", "/"),
            listOf("4", "5", "6", "*"),
            listOf("1", "2", "3", "-"),
            listOf("0", "C", "=", "+")
        )

        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { buttonText ->
                    Button(
                        onClick = {
                            when (buttonText) {
                                "C" -> state.onClearPressed()
                                "=" -> state.onEqualPressed()
                                "+", "-", "*", "/" -> state.onOperatorPressed(buttonText)
                                else -> state.onNumberPressed(buttonText)
                            }
                        },
                        modifier = Modifier
                            .padding(10.dp)
                            .size(80.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6200EE),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = buttonText, fontSize = 30.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    CalculadoraTheme {
        CalculatorUI()
    }
}
