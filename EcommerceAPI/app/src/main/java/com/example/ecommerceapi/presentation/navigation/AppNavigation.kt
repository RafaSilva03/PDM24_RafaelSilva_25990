package com.example.ecommerceapi.presentation.navigation

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
import com.example.ecommerceapi.presentation.menu.MenuScreen

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
                    navController.navigate("menu")
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

        composable("menu") {
            MenuScreen(
                onNavigateToProducts = { navController.navigate("products") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }

    }
}