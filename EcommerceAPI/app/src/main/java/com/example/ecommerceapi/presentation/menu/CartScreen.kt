package com.example.ecommerceapi.presentation.menu


import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.ecommerceapi.Model.CartProduct
import com.example.ecommerceapi.data.firebase.FirebaseHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartItems: List<CartProduct>,
    totalPrice: Double,
    onCheckoutClick: () -> Unit,
    cartId: Int,
    onBackClick: () -> Unit,
    onExportCartClick: () -> Unit,
    onImportCartClick: (Int) -> Unit
) {
    var showImportDialog by remember { mutableStateOf(false) }
    var showCartIdDialog by remember { mutableStateOf(false) }
    var exportCartId by remember { mutableStateOf(0) }
    var importCartId by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    var menuExpanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                menuExpanded = false
                                onExportCartClick()
                                showCartIdDialog = true
                            },
                            text = { Text("Exportar Carrinho") }
                        )
                        DropdownMenuItem(
                            onClick = {
                                menuExpanded = false
                                showImportDialog = true
                            },
                            text = { Text("Importar Carrinho") }
                        )
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TOTAL (incl. imposto)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Text(
                            text = "€${"%.2f".format(totalPrice)}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Button(
                        onClick = onCheckoutClick,
                        modifier = Modifier
                            .height(50.dp)
                            .width(150.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "PAGAMENTO", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(cartItems) { cartItem ->
                CartItemCard(cartItem)
            }
        }

        if (showImportDialog) {
            AlertDialog(
                onDismissRequest = { showImportDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        showImportDialog = false
                        if (importCartId.isNotEmpty()) {
                            onImportCartClick(importCartId.toIntOrNull() ?: 0)
                        }
                    }) {
                        Text("Importar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showImportDialog = false }) {
                        Text("Cancelar")
                    }
                },
                title = { Text("Importar Carrinho") },
                text = {
                    Column {
                        Text("Insira o código do carrinho a importar:")
                        TextField(
                            value = importCartId,
                            onValueChange = { importCartId = it },
                            label = { Text("Código do Carrinho") }
                        )
                    }
                }
            )
        }


        // Exibe o diálogo com o CartId
        if (showCartIdDialog) {
            AlertDialog(
                onDismissRequest = { showCartIdDialog = false },
                confirmButton = {
                    TextButton(onClick = { showCartIdDialog = false }) {
                        Text("Fechar")
                    }
                },
                title = { Text("Exportar Carrinho") },
                text = { Text("Codigo para expotar: $cartId") }
            )
        }
    }
}

@Composable
fun CartItemCard(cartItem: CartProduct) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagem do produto
            Image(
                painter = rememberAsyncImagePainter(cartItem.product.imageUrl),
                contentDescription = cartItem.product.name,
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            // Detalhes do produto
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = cartItem.product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tamanho: ${cartItem.size} - Quantidade: ${cartItem.quantity}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Text(
                        text = "€${"%.2f".format(cartItem.product.price)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}