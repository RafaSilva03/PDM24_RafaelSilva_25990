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
import androidx.compose.ui.Modifier
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
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import domain.use_case.GetNewsUseCase
import data.repository.NewsRepositoryImpl
import data.RetrofitClient
import androidx.compose.material3.Text

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    NewsAPITheme {
        val navController = rememberNavController()


        val newsApi = RetrofitClient.getNewsApi()
        val newsRepository = NewsRepositoryImpl(newsApi)
        val getNewsUseCase = GetNewsUseCase(newsRepository)

        val newsListViewModel: NewsListViewModel = ViewModelProvider(
            LocalContext.current as ComponentActivity,
            NewsListViewModelFactory(getNewsUseCase, newsRepository)
        ).get(NewsListViewModel::class.java)

        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            NavHost(navController = navController, startDestination = "news_list") {
                composable("news_list") {
                    // Passa o ViewModel para a tela de lista
                    NewsListScreen(viewModel = newsListViewModel, navController = navController)
                }
                composable(
                    route = "news_detail/{title}",
                    arguments = listOf(navArgument("title") { type = NavType.StringType })
                ) { backStackEntry ->
                    val title = backStackEntry.arguments?.getString("title")
                    if (!title.isNullOrEmpty()) {
                        // Passa o mesmo ViewModel para a tela de detalhes
                        NewsDetailScreen(title = title, viewModel = newsListViewModel)
                    } else {
                        Text("Erro: título da notícia não foi encontrado.")
                    }
                }
            }
        }
    }
}
