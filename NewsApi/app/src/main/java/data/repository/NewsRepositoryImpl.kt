package data.repository

import data.remote.api.NewsApi
import domain.repository.NewsRepository
import domain.model.News
import com.example.newsapi.BuildConfig
import android.util.Log

class NewsRepositoryImpl(
    private val api: NewsApi
) : NewsRepository {

    override suspend fun getNews(section: String): List<News> {
        val apiKey = BuildConfig.NYT_API_KEY

        // Fazendo a chamada à API
        val response = api.getNews(section = section, apiKey = apiKey)

        // Logando a resposta da API para depuração
        Log.d("NewsApiResponse", response.toString())

        // Convertendo a resposta para a lista de News
        return response.results.map { dto ->
            News(
                id =  dto.id,
                title = dto.title,
                url = dto.url,
                byline = dto.byline ?: "",
                abstract = dto.abstract,
                description = dto.description
            )
        }
    }
}
