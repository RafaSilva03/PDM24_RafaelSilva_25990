package data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import data.remote.api.NewsApi
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://api.nytimes.com/svc/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getNewsApi(): NewsApi {
        return retrofit.create(NewsApi::class.java)
    }
}
