package data.repository

import data.remote.api.NewsApi
import domain.repository.NewsRepository
import domain.model.News
import com.example.newsapi.BuildConfig
import android.util.Log
import data.remote.model.ArticleDoc

class NewsRepositoryImpl(
    private val api: NewsApi
) : NewsRepository {

    override suspend fun getNews(section: String): List<News> {
        val apiKey = BuildConfig.NYT_API_KEY

        Log.d("API_KEY_TEST", "Key being sent: $apiKey")

        val response = api.getNews(section = section, apiKey = apiKey)

        Log.d("NewsApiResponse", response.toString())

        return response.results.map { dto ->
            News(
                id =  dto.id,
                title = dto.title,
                url = dto.url,
                byline = dto.byline ?: "",
                abstract = dto.abstract,
                description = dto.description
            )
        }.also {
            it.forEach { news -> Log.d("Repository", "News ID: ${news.id}, Title: ${news.title}, abstract: ${news.abstract}") }
        }
    }

    override suspend fun getArticleDetails(query: String): ArticleDoc? {
        val response = api.searchArticle(query, BuildConfig.NYT_API_KEY)
        return response.response.docs.firstOrNull()
    }
}
