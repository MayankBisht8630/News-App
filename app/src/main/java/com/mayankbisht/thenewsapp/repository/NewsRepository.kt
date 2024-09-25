package com.mayankbisht.thenewsapp.repository

import com.mayankbisht.thenewsapp.api.RetrofitInstance
import com.mayankbisht.thenewsapp.db.ArticleDatabase
import com.mayankbisht.thenewsapp.models.Article

class NewsRepository(val db: ArticleDatabase) {
    suspend fun getHeadlines(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getHeadLines(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    suspend fun upsert(article: Article) = db.getArticleDAO().upsert(article)

    fun getFavouriteNews() = db.getArticleDAO().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDAO().deleteArticle(article)
}