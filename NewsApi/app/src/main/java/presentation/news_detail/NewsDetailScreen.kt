package presentation.news_detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import domain.model.News
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController
import coil.compose.AsyncImage
import data.remote.model.ArticleDoc
import presentation.news_list.NewsListViewModel

@Composable
fun NewsDetailScreen(title: String, viewModel: NewsListViewModel) {
    var articleDetails by remember { mutableStateOf<ArticleDoc?>(null) }

    LaunchedEffect(title) {
        articleDetails = viewModel.getArticleDetails(title)
    }

    if (articleDetails != null) {
        val details = articleDetails!!

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Título
            Text(
                text = details.headline.main ?: "Título não disponível",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Corpo do texto
            Text(
                text = details.lead_paragraph ?: "Descrição não disponível",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Botão para abrir o link
            val context = LocalContext.current
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(details.web_url))
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ler notícia completa")
            }
        }
    } else {
        // Estado de carregamento
        Text(
            text = "Carregando detalhes da notícia...",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}