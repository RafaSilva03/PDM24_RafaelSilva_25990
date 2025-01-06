package com.example.ecommerceapi.presentation.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.ecommerceapi.R
import com.example.ecommerceapi.presentation.navigation.BottomNavigationBar
import com.example.ecommerceapi.Model.Category

@Composable
fun ComprarScreen(
    navController: NavHostController,
    onCategoryClick: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf("Comprar") }


    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                navController = navController
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            HeaderSection()

            Spacer(modifier = Modifier.height(16.dp))

            CategoriesSection(
                categories = listOf(
                    Category("Sapatilhas", R.drawable.sneakers),
                    Category("Vestuário", R.drawable.clothes),
                    Category("Acessórios", R.drawable.acessorios)
                ),

                onCategoryClick = onCategoryClick
            )

            Spacer(modifier = Modifier.height(24.dp))

        }
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Comprar",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
fun CategoriesSection(
    categories: List<Category>,
    onCategoryClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp) // Espaçamento geral horizontal

    ) {
        categories.forEachIndexed { index, category -> // Usa forEachIndexed para controlar o último elemento
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color.DarkGray, RoundedCornerShape(8.dp)) // Define a borda escura
                    .background(Color.White, RoundedCornerShape(8.dp)) // Fundo branco para destacar
                    .padding(16.dp) // Espaçamento interno
                    .clickable { // Adicione o evento de clique aqui
                     onCategoryClick(category.name)
                    },
                verticalAlignment = Alignment.CenterVertically // Alinha o conteúdo verticalmente
            ) {
                // Texto no lado esquerdo
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f), // Ocupa o espaço restante
                    color = Color.Black
                )

                // Imagem no lado direito
                Image(
                    painter = painterResource(id = category.imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(90.dp) // Define o tamanho da imagem
                        .padding(start = 8.dp), // Espaçamento entre texto e imagem
                    contentScale = ContentScale.Crop // Ajusta a imagem para cobrir o espaço
                )
            }

            // Adiciona espaçamento entre categorias, exceto após a última
            if (index < categories.size - 1) {
                Spacer(modifier = Modifier.height(20.dp)) // Espaço entre categorias
            }
        }
    }
}

