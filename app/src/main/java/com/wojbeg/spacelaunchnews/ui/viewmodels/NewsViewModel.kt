package com.wojbeg.spacelaunchnews.ui.viewmodels


import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wojbeg.spacelaunchnews.R
import com.wojbeg.spacelaunchnews.SpaceNewsApplication
import com.wojbeg.spacelaunchnews.repository.ArticleRepository
import com.wojbeg.spacelaunchnews.models.Article
import com.wojbeg.spacelaunchnews.repository.RoomRepository
import com.wojbeg.spacelaunchnews.util.ClassTypes
import com.wojbeg.spacelaunchnews.util.Constants
import com.wojbeg.spacelaunchnews.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val app: Application,
    private val repository: ArticleRepository,
    private val roomRepository: RoomRepository,
) : AndroidViewModel(app) {

    //val internetConnection = MutableLiveData<Boolean>()

    val reportsLiveData = MutableLiveData<Resource<MutableList<Article>>>()

    private var totalArticles = 0
    private var currentArticlesCount = 0
    private var articlesList: MutableList<Article> = mutableListOf<Article>()

    val searchLiveData = MutableLiveData<Resource<MutableList<Article>>>()
    private var currentSearchArticlesCount = 0
    private var searchArticlesList: MutableList<Article> = mutableListOf<Article>()

    private var prevFragmentNum: ClassTypes = ClassTypes.NONE
    private val resources = app.resources

    init {
        getArticles()
        getArticlesCount()
    }

    //Retrofit section - get information form endpoints

    fun getArticlesCount() = viewModelScope.launch {
        if(hasInternetConnection()){
            val response = repository.getArticlesCount()

            if(response.isSuccessful){

                response.body()?.let {
                        result ->
                    totalArticles = result

                }
            }
        }else{
            reportsLiveData.postValue(Resource.Error(resources.getString(R.string.no_internet)))
        }

    }

    fun getArticles() = viewModelScope.launch {
        getArticlesCall()
    }

    fun getSearchNews(searchQuery: String) = viewModelScope.launch {

        getSearchArticlesCall(searchQuery)
    }

    private suspend fun getArticlesCall(){
        reportsLiveData.postValue(Resource.Loading())

        try {

            if(hasInternetConnection()){
                val response = repository.getArticles(Constants.QUERY_PAGE_SIZE, currentArticlesCount)
                reportsLiveData.postValue(handleResponse(response))
            }else{
                reportsLiveData.postValue(Resource.Error(resources.getString(R.string.no_internet)))
            }

        }catch (t: Throwable){
            reportsLiveData.postValue(Resource.Error(resources.getString(R.string.error_loading_resources)))
        }

    }

    private fun handleResponse(response: Response<MutableList<Article>>): Resource<MutableList<Article>>{
        if(response.isSuccessful){
           response.body()?.let {
               resultResponse ->
               currentArticlesCount += resultResponse.size

               if(articlesList.isEmpty()){
                   articlesList = resultResponse
               }else{
                   articlesList.addAll(resultResponse)
               }
               return Resource.Success(articlesList)
           }
        }
        return Resource.Error(response.message())
    }

    fun isLastPage() : Boolean =
        currentArticlesCount == totalArticles


    private suspend fun getSearchArticlesCall(searchQuery: String){
        reportsLiveData.postValue(Resource.Loading())

        try {
            if(hasInternetConnection()){
                val response = repository.getSearchArticles(searchQuery, Constants.QUERY_PAGE_SIZE, currentSearchArticlesCount)
                searchLiveData.postValue(handleSearchResponse(response))
            }else{
                searchLiveData.postValue(Resource.Error(resources.getString(R.string.no_internet)))
            }

            }catch (t: Throwable){
            reportsLiveData.postValue(Resource.Error(resources.getString(R.string.error_loading_resources)))
        }

    }

    private fun handleSearchResponse(response: Response<MutableList<Article>>): Resource<MutableList<Article>>{


        if(response.isSuccessful){

            response.body()?.let {
                    resultResponse ->
                currentSearchArticlesCount += resultResponse.size

                if(searchArticlesList.isEmpty()){
                    searchArticlesList = resultResponse
                }else{

                    searchArticlesList.addAll(resultResponse)

                }
                return Resource.Success(searchArticlesList)
            }
        }
        return Resource.Error(response.message())
    }

    fun clearArticles(){
        totalArticles = 0
        currentArticlesCount = 0
        articlesList.clear()

    }

    fun refreshArticles(){

        getArticles()
        getArticlesCount()
    }

    fun clearSearch(){
        currentSearchArticlesCount = 0
        searchArticlesList.clear()
    }

    //Room section - saving on local storage database


    fun saveArticle(article: Article) = viewModelScope.launch {
       roomRepository.insert(article)
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        roomRepository.deleteArticle(article)
    }

    fun getSavedArticles() = roomRepository.getSavedNews()


    fun setPrevFragmentNum(fragment: ClassTypes) {
        prevFragmentNum = fragment
    }

    fun getPrevFragmentNum(): ClassTypes =
        prevFragmentNum

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<SpaceNewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }else{
                connectivityManager.activeNetworkInfo?.run {
                    return when(type){
                        TYPE_WIFI -> true
                        TYPE_MOBILE -> true
                        TYPE_ETHERNET -> true
                        else -> false
                    }
                }
        }
        return false

    }

}