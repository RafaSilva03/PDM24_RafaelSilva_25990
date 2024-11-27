package domain.model

data class News(
    val id: String,
    val title: String,
    val url: String,
    val byline: String,
    val abstract: String,
    val imageUrl: String? = null
)