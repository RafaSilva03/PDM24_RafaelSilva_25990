package data.remote.model

data class NewsDetailDto(
    val id: String,
    val title: String,
    val url: String,
    val byline: String?,
    val abstract: String,
    val description: String
)