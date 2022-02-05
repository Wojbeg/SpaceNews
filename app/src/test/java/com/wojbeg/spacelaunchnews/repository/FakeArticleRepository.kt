package com.wojbeg.spacelaunchnews.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wojbeg.spacelaunchnews.models.Article
import com.wojbeg.spacelaunchnews.util.Resource
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

class FakeArticleRepository : ArticleRepositoryInterface, RoomRepositoryInterface{

    private val articleItems = mutableListOf<Article>()

    private val observableArticleItems = MutableLiveData<List<Article>>(articleItems)

    private var shouldReturnNetworkError = false

    fun setShouldReturnNetworkError(value: Boolean){
        shouldReturnNetworkError = value
    }

    private fun refreshLiveData(){
        observableArticleItems.postValue(articleItems)

    }


    override suspend fun getArticles(limit: Int, startPoint: Int): Response<MutableList<Article>> {

        return if(shouldReturnNetworkError){
            Response.error(1, ResponseBody.create("error".toMediaType(),"Error getArticles"))
        }else{
            return Response.success(mutableListOf<Article>())
        }

    }

    override suspend fun getSearchArticles(
        searchQuery: String,
        limit: Int,
        startPoint: Int
    ): Response<MutableList<Article>> {

        return if(shouldReturnNetworkError){
            Response.error(1, ResponseBody.create("error".toMediaType(),"Error getSearchArticles"))
        }else{
            Response.success(mutableListOf())
        }
    }

    override suspend fun getArticlesCount(): Response<Int> {
        return Response.success(articleItems.count())
    }


    //Room

    override suspend fun insert(article: Article): Long {
        articleItems.add(article)
        refreshLiveData()
        //
        return article.toString().toLong()
    }

    override fun getSavedNews(): LiveData<List<Article>> {
        return observableArticleItems
    }

    override suspend fun deleteArticle(article: Article) {
        articleItems.remove(article)
        refreshLiveData()
    }
}