package data.repository

import data.remote.api.NewsApi
import domain.repository.NewsRepository
import domain.model.News
import com.example.newsapi.BuildConfig



class NewsRepositoryImpl(
    private val api: NewsApi
) : NewsRepository {

    override suspend fun getNews(section: String): List<News> {
        val apiKey = BuildConfig.NYT_API_KEY
        val response = api.getNews(section = section, apiKey = apiKey)
        return response.results.map { dto ->
            News(
                id =  dto.id,
                title = dto.title,
                url = dto.url,
                byline = dto.byline ?: "",
                abstract = dto.abstract
            )
        }
    }
}
