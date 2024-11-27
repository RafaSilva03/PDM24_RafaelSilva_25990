package domain.repository

import domain.model.News

interface NewsRepository {
    suspend fun getNews(section: String): List<News>
}