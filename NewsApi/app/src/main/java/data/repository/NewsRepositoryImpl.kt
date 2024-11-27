package data.repository

import data.remote.api.NewsApi
import domain.repository.NewsRepository
import domain.model.News

class NewsRepositoryImpl(
    private val api: NewsApi
) : NewsRepository {

    companion object {
        private const val API_KEY = "jEtKE5O5EHhP2HzKHlobbUqe6VkGnqGj"
    }

    override suspend fun getNews(section: String): List<News> {
        val response = api.getNews(section = section, apiKey = API_KEY)
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
