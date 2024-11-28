package presentation.news_detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import domain.model.News

@Composable
fun NewsDetailScreen(news: News) {
    // A coluna irá conter os detalhes da notícia
    Column(
        modifier = androidx.compose.ui.Modifier.padding(16.dp)  // Adiciona padding nas bordas
    ) {
        // Exibe o título da notícia em um estilo mais destacado
        Text(
            text = news.title,
            style = MaterialTheme.typography.headlineMedium,  // Estilo de título
        )

        Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))  // Espaçamento entre o título e o abstract

        // Exibe o abstract da notícia
        Text(
            text = news.abstract,
            style = MaterialTheme.typography.bodyLarge  // Estilo para o texto do abstract
        )

        Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))  // Espaçamento adicional após o abstract

        // Caso você queira exibir uma imagem (se a API fornecer a URL da imagem)
        // Image(painter = rememberImagePainter(news.imageUrl), contentDescription = "News Image")
    }
}
