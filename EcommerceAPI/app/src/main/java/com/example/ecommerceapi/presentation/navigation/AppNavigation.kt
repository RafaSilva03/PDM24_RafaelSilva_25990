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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ecommerceapi.presentation.login.LoginScreen
import com.example.ecommerceapi.presentation.register.RegisterScreen
import com.example.ecommerceapi.presentation.menu.ComprarScreen
import com.example.ecommerceapi.presentation.menu.ProductsScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    val showBottomNav = remember { mutableStateOf(false) } // Gerencia visibilidade do BottomNav

    NavHost(
        navController = navController,
        startDestination = "comprar",
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

        // Tela de Carrinho
        composable("carrinho") {

        }

        // Tela de Perfil
        composable("perfil") {

        }

        composable("products/{category}") { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            Log.d("Categoria", "Navegando para categoria: $category") // Debug
            ProductsScreen(
                category = category,
                onBackClick = { navController.popBackStack() }
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