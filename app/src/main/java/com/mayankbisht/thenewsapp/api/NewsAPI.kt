package com.mayankbisht.thenewsapp.api

import com.mayankbisht.thenewsapp.models.NewsResponse
import com.mayankbisht.thenewsapp.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {
    @GET("v2/top-headlines")
    suspend fun getHeadLines(
        @Query("country")
        countryCode: String = "us",

        @Query("page")
        pageNumber: Int = 1,

        @Query("apikey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        searchQuery: String,

        @Query("page")
        pageNumber: Int = 1,

        @Query("apikey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>
}