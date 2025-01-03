package data.remote.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import data.remote.model.NewsDto
import data.remote.model.ArticleSearch


interface NewsApi {
    @GET("topstories/v2/{section}.json")
    suspend fun getNews(
        @Path("section") section: String,
        @Query("api-key") apiKey: String
    ): NewsDto

    @GET("search/v2/articlesearch.json")
    suspend fun searchArticle(
        @Query("q") query: String,
        @Query("api-key") apiKey: String
    ): ArticleSearch
}