package domain.repository

import com.example.newsapi.BuildConfig
import domain.model.News
import data.remote.model.ArticleDoc

interface NewsRepository {
    suspend fun getNews(section: String): List<News>

    suspend fun getArticleDetails(query: String): ArticleDoc?
}