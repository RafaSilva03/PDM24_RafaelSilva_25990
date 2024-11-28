package presentation.news_list

import android.util.Log
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.gson.Gson
import domain.model.News

@Composable
fun NewsListScreen(viewModel: NewsListViewModel, navController: NavController) {
    // Observe o LiveData da lista de notícias usando observeAsState
    val newsList by viewModel.news.observeAsState(emptyList())

    // Verifique se a lista de notícias está vazia, e faça o fetch das notícias se necessário
    LaunchedEffect(Unit) {
        viewModel.fetchNews("home")  // ou qualquer seção que você deseje
    }

    // Exibe a lista de notícias
    Column(modifier = Modifier.fillMaxWidth()) {
        newsList.forEach { news ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable {
                    // Serializa o objeto News em JSON
                    val newsJson = Gson().toJson(news)
                    // Navega para a tela de detalhes, passando o JSON
                    navController.navigate("news_detail/$newsJson")
                }
            ) {
                // Exibe o título da notícia
                Text(text = news.title)  // Corrige o erro de ambiguidade, garantindo que 'title' é uma String
            }
        }
    }
}
