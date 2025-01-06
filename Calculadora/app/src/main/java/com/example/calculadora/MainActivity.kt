package com.example.calculadora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.calculadora.ui.theme.CalculadoraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculadoraTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        CalculatorUI()
                    }
                }
            }
        }
    }
}

@Composable
fun rememberCalculatorState(): CalculatorState {
    var displayText by remember { mutableStateOf("0") }
    var currentNumber by remember { mutableStateOf("") }
    var lastResult by remember { mutableStateOf(0.0) }
    var lastOperator by remember { mutableStateOf<String?>(null) }
    var isLastOperationEquals by remember { mutableStateOf(false) }

    // Local function for handling "="
    fun handleEqualPressed() {
        if (currentNumber.isNotEmpty() && lastOperator != null) {
            val currentNumberValue = currentNumber.toDouble()
            lastResult = calculate(lastResult, currentNumberValue, lastOperator!!)
            displayText = lastResult.toString()
            currentNumber = ""
            lastOperator = null
            isLastOperationEquals = true
        }
    }

    return CalculatorState(
        displayText = displayText,
        onNumberPressed = { number ->
            if (isLastOperationEquals) {
                currentNumber = ""
                lastOperator = null
                isLastOperationEquals = false
            }
            currentNumber += number
            displayText = currentNumber
        },
        onOperatorPressed = { operator ->
            if (currentNumber.isNotEmpty()) {
                val currentNumberValue = currentNumber.toDouble()
                lastResult = if (lastOperator != null) {
                    calculate(lastResult, currentNumberValue, lastOperator!!)
                } else {
                    currentNumberValue
                }
                displayText = lastResult.toString()
                currentNumber = ""
            }
            lastOperator = operator
            if (operator == "+") {
                handleEqualPressed()  // Call the local handleEqualPressed function
            }
        },
        onClearPressed = {
            currentNumber = ""
            lastResult = 0.0
            lastOperator = null
            displayText = "0"
            isLastOperationEquals = false
        },
        onEqualPressed = {
            handleEqualPressed()  // Call the local handleEqualPressed function
        }
    )
}

fun calculate(num1: Double, num2: Double, operator: String): Double {
    return when (operator) {
        "+" -> num1 + num2
        "-" -> num1 - num2
        "*" -> num1 * num2
        "/" -> num1 / num2
        else -> num2
    }
}

data class CalculatorState(
    val displayText: String,
    val onNumberPressed: (String) -> Unit,
    val onOperatorPressed: (String) -> Unit,
    val onClearPressed: () -> Unit,
    val onEqualPressed: () -> Unit
)
