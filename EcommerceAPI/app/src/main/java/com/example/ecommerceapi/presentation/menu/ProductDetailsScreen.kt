package com.example.ecommerceapi.presentation.menu

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.ecommerceapi.data.firebase.FirebaseHelper
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    productId: Int,
    productName: String,
    productPrice: Double,
    onBackClick: () -> Unit
) {
    val firebaseHelper = remember { FirebaseHelper() }
    var productQuantities by remember { mutableStateOf<List<Pair<Int, Int>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedSize by remember { mutableStateOf<Int?>(null) }
    var imageUrl by remember { mutableStateOf("") }

    // Busca os tamanhos disponíveis no Firestore
    LaunchedEffect(productId) {
        isLoading = true
        firebaseHelper.getProductQuantities(
            productId = productId,
            onSuccess = { quantities ->
                productQuantities = quantities.distinct().sortedBy { it.first }
                isLoading = false
            },
            onFailure = { exception ->
                Log.e("ProductDetails", "Erro ao carregar tamanhos: ${exception.message}")
                isLoading = false
            }
        )

        firebaseHelper.getProductImage(
            productId = productId,
            onSuccess = { url ->
                imageUrl = url
            },
            onFailure = { exception ->
                Log.e("ProductDetails", "Erro ao carregar imagem: ${exception.message}")
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Exibir imagem do produto
                    if (imageUrl.isNotEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = productName,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(MaterialTheme.shapes.medium),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Exibir nome e preço do produto
                    Text(
                        text = productName,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "€$productPrice",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Exibir tamanhos disponíveis
                    Text(
                        text = "Escolha o tamanho:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        productQuantities.forEach { (size, _) ->
                            OutlinedButton(
                                onClick = { selectedSize = size },
                                border = BorderStroke(
                                    1.dp,
                                    if (selectedSize == size) MaterialTheme.colorScheme.primary else Color.Gray
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (selectedSize == size) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.weight(1f) // Garante que os botões tenham o mesmo tamanho
                            ) {
                                Text(text = "$size", color = if (selectedSize == size) MaterialTheme.colorScheme.primary else Color.Black)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            selectedSize?.let { size ->
                                val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
                                // Verificar ou criar o CartId antes de adicionar o produto
                                firebaseHelper.getUserCartId(
                                    userEmail = currentUserEmail,
                                    onSuccess = { cartId ->
                                        if (cartId != null) {
                                            // Adicionar ao carrinho existente
                                            firebaseHelper.addProductToCart(
                                                cartId = cartId,
                                                productId = productId,
                                                size = size,
                                                quantity = 1,
                                                onSuccess = {
                                                    Log.d("Carrinho", "Produto adicionado com sucesso: $productId, Tamanho: $size")
                                                },
                                                onFailure = { exception ->
                                                    Log.e("Carrinho", "Erro ao adicionar produto ao carrinho: ${exception.message}")
                                                }
                                            )
                                        } else {
                                            // Criar um novo carrinho
                                            firebaseHelper.createCartForUser(
                                                userEmail = currentUserEmail,
                                                onSuccess = { newCartId ->
                                                    firebaseHelper.addProductToCart(
                                                        cartId = newCartId,
                                                        productId = productId,
                                                        size = size,
                                                        quantity = 1,
                                                        onSuccess = {
                                                            Log.d("Carrinho", "Produto adicionado com sucesso ao novo carrinho.")
                                                        },
                                                        onFailure = { exception ->
                                                            Log.e("Carrinho", "Erro ao adicionar produto ao novo carrinho: ${exception.message}")
                                                        }
                                                    )
                                                },
                                                onFailure = { exception ->
                                                    Log.e("Carrinho", "Erro ao criar carrinho: ${exception.message}")
                                                }
                                            )
                                        }
                                    },
                                    onFailure = { exception ->
                                        Log.e("Carrinho", "Erro ao obter CartId: ${exception.message}")
                                    }
                                )
                            }
                        },
                        enabled = selectedSize != null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Adicionar ao Carrinho")
                    }
                }
            }
        }
    }
}
