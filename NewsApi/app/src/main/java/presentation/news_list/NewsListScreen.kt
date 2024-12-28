package presentation.news_list

import android.util.Log
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.gson.Gson
import domain.model.News

@Composable
fun NewsListScreen(viewModel: NewsListViewModel, navController: NavController) {

    val newsList by viewModel.news.observeAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        viewModel.fetchNews("home")
        newsList.forEach { news ->
            Log.d("NewsListScreen", "ID: ${news.id}, Title: ${news.title}")
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        newsList.forEach { news ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        navController.navigate("news_detail/${news.title}")
                    }
            ) {
                Text(
                    text = news.title,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
