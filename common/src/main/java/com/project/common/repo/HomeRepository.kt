package com.project.common.repo

import com.project.common.data_source.ApiService
import com.project.common.model.HomeResponseItem
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getHomeData(): Response<List<HomeResponseItem>> {
        return apiService.getHomeData()
    }
}
