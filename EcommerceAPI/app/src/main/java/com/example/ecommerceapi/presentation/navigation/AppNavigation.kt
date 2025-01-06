package com.example.ecommerceapi.presentation.navigation

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ecommerceapi.data.firebase.FirebaseHelper
import com.example.ecommerceapi.Model.CartProduct
import com.example.ecommerceapi.presentation.login.LoginScreen
import com.example.ecommerceapi.presentation.register.RegisterScreen
import com.example.ecommerceapi.presentation.menu.ComprarScreen
import com.example.ecommerceapi.presentation.menu.ProductsScreen
import com.example.ecommerceapi.presentation.menu.ProductDetailsScreen
import com.example.ecommerceapi.presentation.menu.CartScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation(navController: NavHostController) {
    val showBottomNav = remember { mutableStateOf(false) } // Gerencia visibilidade do BottomNav

    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = Modifier
    ) {
        // Tela de Login
        composable("login") {
            showBottomNav.value = false
            LoginScreen(
                onLoginSuccess = {
                    showBottomNav.value = true
                    navController.navigate("comprar")
                },
                onCreateAccountClick = {
                    navController.navigate("register")
                }
            )
        }


        // Tela de Registro
        composable("register") {
            showBottomNav.value = false
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("login")
                }
            )
        }

        // Menu Principal
        composable("comprar") {
            ComprarScreen(
                navController = navController, // Certifique-se de passar o navController
                onCategoryClick = { category ->
                    Log.d("Categoria", "Categoria clicada: $category")
                    navController.navigate("products/$category")
                }
            )
        }


        // Tela de Perfil
        composable("perfil") {

        }

        composable("products/{category}") { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            ProductsScreen(
                category = category,
                onBackClick = { navController.popBackStack() },
                onProductClick = { product ->
                    Log.d("Navegação", "ID: ${product.id}, Name: ${product.name}")
                    navController.navigate("productDetails/${product.id}/${product.name}/${product.price}")
                }
            )
        }

        composable("productDetails/{productId}/{productName}/{productPrice}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")!!.toInt()
            val productName = backStackEntry.arguments?.getString("productName") ?: ""
            val productPrice = backStackEntry.arguments?.getString("productPrice")!!.toDouble()
            ProductDetailsScreen(
                productId = productId,
                productName = productName,
                productPrice = productPrice,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("Carrinho") {
            val firebaseHelper = FirebaseHelper()
            val cartProducts = remember { mutableStateOf<List<CartProduct>>(emptyList()) }
            val totalPrice = remember { mutableStateOf(0.0) }
            val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

            if (currentUserEmail.isNullOrEmpty()) {
                // Mostre uma mensagem ou redirecione o usuário para o login
                Text("Usuário não autenticado. Por favor, faça login.")
                return@composable
            }

            // Busca ou cria o CartID para o usuário
            LaunchedEffect(Unit) {
                firebaseHelper.getUserCartId(
                    userEmail = currentUserEmail,
                    onSuccess = { cartId ->
                        if (cartId != null) {
                            // Busca os itens do carrinho usando o CartID
                            firebaseHelper.getCartProducts(
                                cartId = cartId,
                                onSuccess = { products ->
                                    cartProducts.value = products
                                    totalPrice.value = products.sumOf { it.product.price * it.quantity }
                                },
                                onFailure = {
                                    cartProducts.value = emptyList() // Mostre uma mensagem de erro ou deixe vazio
                                }
                            )
                        } else {
                            // Se não existir um CartID, cria um novo
                            firebaseHelper.createCartForUser(
                                userEmail = currentUserEmail,
                                onSuccess = { newCartId ->
                                    // Carrinho criado, agora busca os itens do carrinho (provavelmente vazio)
                                    firebaseHelper.getCartProducts(
                                        cartId = newCartId,
                                        onSuccess = { products ->
                                            cartProducts.value = products
                                            totalPrice.value = products.sumOf { it.product.price * it.quantity }
                                        },
                                        onFailure = {
                                            cartProducts.value = emptyList() // Mostre uma mensagem de erro ou deixe vazio
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
                        Log.e("Carrinho", "Erro ao obter CartID: ${exception.message}")
                    }
                )
            }

            CartScreen(
                cartItems = cartProducts.value,
                totalPrice = totalPrice.value,
                onCheckoutClick = {
                    // Navegação para o processo de pagamento ou confirmação
                }
            )
        }

    }
}

@Composable
fun BottomNavigationBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    navController: NavHostController
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedTab == "Comprar",
            onClick = {
                onTabSelected("Comprar")
                navController.navigate("comprar")
            },
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Comprar") },
            label = { Text("Comprar") }
        )
        NavigationBarItem(
            selected = selectedTab == "Carrinho",
            onClick = {
                onTabSelected("Carrinho")
                navController.navigate("carrinho")
            },
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Carrinho") },
            label = { Text("Carrinho") }
        )
        NavigationBarItem(
            selected = selectedTab == "Perfil",
            onClick = {
                onTabSelected("Perfil")
                navController.navigate("perfil")
            },
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") }
        )
    }
}