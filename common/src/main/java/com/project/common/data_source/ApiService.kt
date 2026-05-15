package com.project.common.data_source

import com.project.common.model.HomeResponseItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {

    @GET("api/v1/tags/screens/")
    suspend fun getHomeData(
        @Header("X-App-Secret") appSecret: String = "PTxXEHQks61dwJGvShSSw1VnQe5xxX0eTWzAnftVbtV1cvFAkKXw7nXYRkBmzb3n",
        @Query("key") key: String = "listing",
        @Query("fields") fields: String = "categories[frames]"
    ): Response<List<HomeResponseItem>>

    companion object {
        const val BASE_URL = "https://editrix.themultiversetech.com/"
    }
}
