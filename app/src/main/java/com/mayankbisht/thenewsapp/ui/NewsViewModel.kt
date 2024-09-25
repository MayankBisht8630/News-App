package com.mayankbisht.thenewsapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mayankbisht.thenewsapp.models.Article
import com.mayankbisht.thenewsapp.models.NewsResponse
import com.mayankbisht.thenewsapp.repository.NewsRepository
import com.mayankbisht.thenewsapp.util.Resourse
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(app: Application, val newsRepository: NewsRepository): AndroidViewModel(app) {
    val headlines: MutableLiveData<Resourse<NewsResponse>> = MutableLiveData()
    var headlinesPage = 1
    var headlineResponse: NewsResponse ?= null

    val searchNews: MutableLiveData<Resourse<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse ?= null
    var newSearchQuery: String ?= null
    var oldSearchQuery: String ?= null

    init {
        getHeadlines("us")
    }

    fun getHeadlines(countryCode: String) = viewModelScope.launch {
        headlinesInternet(countryCode)
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNewsInternet(searchQuery)
    }

    private fun handleHeadlineResponse(response: Response<NewsResponse>): Resourse<NewsResponse> {
        if (response.isSuccessful){
            response.body()?.let {
                resultResponse ->
                headlinesPage++

                if (headlineResponse == null){
                    headlineResponse = resultResponse
                } else{
                    val oldArticles = headlineResponse?.articles
                    val newArticles = resultResponse.articles

                    oldArticles?.addAll(newArticles)
                }
                return Resourse.Success(headlineResponse ?: resultResponse)
            }
        }
        return Resourse.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resourse<NewsResponse> {
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                if (searchNewsResponse == null || newSearchQuery != oldSearchQuery){
                    searchNewsPage = 1
                    oldSearchQuery = newSearchQuery
                    searchNewsResponse = resultResponse
                } else{
                    searchNewsPage++
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles

                    oldArticles?.addAll(newArticles)
                }
                return Resourse.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resourse.Error(response.message())
    }

    fun addToFavourite(article: Article) = viewModelScope.launch{
        newsRepository.upsert(article)
    }

    fun getFavouriteNews() = newsRepository.getFavouriteNews()

    fun deleteArticle(article: Article) = viewModelScope.launch{
        newsRepository.deleteArticle(article)
    }

    fun internetConnection(context: Context): Boolean{
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            return getNetworkCapabilities(activeNetwork)?.run {
                when{
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } ?: false
        }
    }

    private suspend fun headlinesInternet(countryCode: String){
        headlines.postValue(Resourse.Loading())
        try{
            if(internetConnection(this.getApplication())){
                val response = newsRepository.getHeadlines(countryCode, headlinesPage)
                headlines.postValue(handleHeadlineResponse(response))
            } else{
                headlines.postValue(Resourse.Error("No Internet Connected !!"))
            }
        } catch (t: Throwable){
            when(t){
                is IOException -> headlines.postValue((Resourse.Error("Unable To Connect")))
                else -> headlines.postValue(Resourse.Error("No Signal"))
            }
        }
    }

    private suspend fun searchNewsInternet(searchQuery: String){
        newSearchQuery = searchQuery
        searchNews.postValue(Resourse.Loading())
        try{
            if(internetConnection(this.getApplication())){
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            } else{
                searchNews.postValue(Resourse.Error("No Internet Connected !!"))
            }
        } catch (t: Throwable){
            when(t){
                is IOException -> searchNews.postValue((Resourse.Error("Unable To Connect")))
                else -> searchNews.postValue(Resourse.Error("No Signal"))
            }
        }
    }
}