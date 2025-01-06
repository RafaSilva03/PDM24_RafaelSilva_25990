package com.example.ecommerceapi.presentation.menu


import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentForm(
    onPaymentConfirm: () -> Unit,
    onBackClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var isFormValid by remember { mutableStateOf(false) }

    // Validação básica
    LaunchedEffect(name, cardNumber, expiryDate, cvv) {
        isFormValid = name.isNotEmpty() && cardNumber.length == 16 && expiryDate.matches(Regex("\\d{2}/\\d{2}")) && cvv.length == 3
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pagamento") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Insira os detalhes do pagamento",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // Nome no Cartão
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome no cartão") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Número do Cartão
            OutlinedTextField(
                value = cardNumber,
                onValueChange = { if (it.length <= 16 && it.all { char -> char.isDigit() }) cardNumber = it },
                label = { Text("Número do cartão") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Data de Validade
            OutlinedTextField(
                value = expiryDate,
                onValueChange = { if (it.length <= 5 && it.matches(Regex("\\d{0,2}/?\\d{0,2}"))) expiryDate = it },
                label = { Text("Data de validade (MM/AA)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // CVV
            OutlinedTextField(
                value = cvv,
                onValueChange = { if (it.length <= 3 && it.all { char -> char.isDigit() }) cvv = it },
                label = { Text("CVV") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onPaymentConfirm,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar Pagamento")
            }
        }
    }
}