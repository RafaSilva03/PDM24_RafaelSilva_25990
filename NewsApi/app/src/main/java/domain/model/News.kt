package domain.model

data class News(
    val id: String? = null,
    val title: String,
    val url: String,
    val byline: String,
    val abstract: String,
    val imageUrl: String? = null,
    val description: String? = null
)