package presentation.news_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import domain.model.News
import domain.use_case.GetNewsUseCase
import kotlinx.coroutines.launch

class NewsListViewModel(
    private val getNewsUseCase: GetNewsUseCase
) : ViewModel() {
    private val _news = MutableLiveData<List<News>>()
    val news: LiveData<List<News>> = _news

    fun fetchNews(section: String) {
        viewModelScope.launch {
            _news.value = getNewsUseCase(section)
        }
    }
}

class NewsListViewModelFactory(
    private val getNewsUseCase: GetNewsUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(NewsListViewModel::class.java)) {
            NewsListViewModel(getNewsUseCase) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
