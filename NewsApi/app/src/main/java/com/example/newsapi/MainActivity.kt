package com.example.newsapi

import android.os.Bundle
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import presentation.news_list.NewsListScreen
import presentation.news_detail.NewsDetailScreen
import domain.model.News
import com.example.newsapi.ui.theme.NewsAPITheme
import androidx.lifecycle.ViewModelProvider
import presentation.news_list.NewsListViewModel
import presentation.news_list.NewsListViewModelFactory
import com.google.gson.Gson
import domain.use_case.GetNewsUseCase
import data.repository.NewsRepositoryImpl
import data.RetrofitClient

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()  // Chama a função MainScreen onde você configura a navegação
        }
    }
}

@Composable
fun MainScreen() {
    NewsAPITheme {
        // Defina o NavController
        val navController = rememberNavController()

        // Defina o layout
        Surface(modifier = androidx.compose.ui.Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            // Configura a navegação entre as telas
            NavHost(navController = navController, startDestination = "news_list") {
                composable("news_list") {
                    // Obtém a instância do Retrofit e cria o repositório
                    val newsApi = RetrofitClient.getNewsApi()
                    val newsRepository = NewsRepositoryImpl(newsApi)
                    val getNewsUseCase = GetNewsUseCase(newsRepository)

                    // Criando o ViewModel usando a ViewModelProvider e a fábrica personalizada
                    val newsListViewModel: NewsListViewModel = ViewModelProvider(
                        LocalContext.current as ComponentActivity,
                        NewsListViewModelFactory(getNewsUseCase) // Passando o getNewsUseCase aqui
                    ).get(NewsListViewModel::class.java)

                    // Passando o ViewModel para a NewsListScreen
                    NewsListScreen(viewModel = newsListViewModel, navController = navController)
                }
                composable("news_detail/{newsJson}") { backStackEntry ->
                    // Recupera o JSON da notícia
                    val newsJson = backStackEntry.arguments?.getString("newsJson")
                    val news = Gson().fromJson(newsJson, News::class.java)
                    // Passa a notícia inteira para a tela de detalhes
                    NewsDetailScreen(news = news)
                }
            }
        }
    }
}
