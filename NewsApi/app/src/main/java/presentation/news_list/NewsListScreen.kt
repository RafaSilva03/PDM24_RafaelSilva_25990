package presentation.news_list
import domain.model.News

import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.clickable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items


@Composable
fun NewsListItem(newsItem: News, navController: NavController) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp) // Usando diretamente o valor de elevação
    ) {
        Column(
            modifier = Modifier
                .clickable {
                    // Navegar para a tela de detalhes quando o item for clicado
                    navController.navigate("news_detail/${newsItem.id.toString()}") // Convertendo id para String
                }
                .padding(16.dp)
        ) {
            // Exibe o título da notícia
            Text(
                text = newsItem.title,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Exibe o abstract da notícia
            Text(
                text = newsItem.abstract,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )

            // Exibe uma imagem da notícia (se a URL da imagem for fornecida)
            newsItem.imageUrl?.let {
                if (it.isNotBlank()) {
                    Image(
                        painter = rememberImagePainter(it),
                        contentDescription = "Image for ${newsItem.title}",
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
fun NewsListScreen(viewModel: NewsListViewModel, navController: NavController) {
    val news = viewModel.news.observeAsState(emptyList()).value


    // Carrega as notícias ao abrir a tela
    LaunchedEffect(Unit) {
        viewModel.fetchNews("world") // Exemplo de seção "world"
    }

    LazyColumn {
        items(news) { newsItem ->
            NewsListItem(newsItem = newsItem, navController = navController)
        }
    }
}
