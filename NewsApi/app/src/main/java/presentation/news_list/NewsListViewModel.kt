package presentation.news_list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import domain.model.News
import domain.use_case.GetNewsUseCase
import kotlinx.coroutines.launch
import data.remote.model.ArticleDoc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import domain.repository.NewsRepository

class NewsListViewModel(
    private val getNewsUseCase: GetNewsUseCase,
    private val newsRepository: NewsRepository
) : ViewModel() {
    private val _news = MutableLiveData<List<News>>()
    val news: LiveData<List<News>> = _news

    fun getNewsById(newsId: String?): News? {
        return news.value?.find { it.id == newsId }
    }

    suspend fun getArticleDetails(title: String): ArticleDoc? {
        return withContext(Dispatchers.IO) {
            try {
                newsRepository.getArticleDetails(title)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun fetchNews(section: String) {
        viewModelScope.launch {
            try {
                _news.value = getNewsUseCase(section)
            } catch (e: Exception) {

                Log.e("NewsListViewModel", "Erro ao buscar notícias: ${e.message}")
            }
        }
    }
}

class NewsListViewModelFactory(
    private val getNewsUseCase: GetNewsUseCase,
    private val newsRepository: NewsRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(NewsListViewModel::class.java)) {
            NewsListViewModel(getNewsUseCase, newsRepository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
