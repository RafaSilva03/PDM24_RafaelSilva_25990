package domain.use_case

import domain.repository.NewsRepository
import domain.model.News

class GetNewsUseCase(private val repository: NewsRepository) {
    suspend operator fun invoke(section: String): List<News> {
        return repository.getNews(section)
    }
}