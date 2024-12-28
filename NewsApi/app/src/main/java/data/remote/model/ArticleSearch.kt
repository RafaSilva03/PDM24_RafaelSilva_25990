package data.remote.model

data class ArticleSearch(
    val response: ResponseData
)

data class ResponseData(
    val docs: List<ArticleDoc>
)

data class ArticleDoc(
    val web_url: String,
    val snippet: String,
    val lead_paragraph: String,
    val headline: Headline
)

data class Headline(
    val main: String
)