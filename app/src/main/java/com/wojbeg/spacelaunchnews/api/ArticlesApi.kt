package com.wojbeg.spacelaunchnews.api

import com.wojbeg.spacelaunchnews.models.Article
import com.wojbeg.spacelaunchnews.util.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ArticlesApi {

    @GET(Constants.END_POINT_ARTICLES)
    suspend fun getArticles(
        @Query("_limit")
        limit: Int,
        @Query("_start")
        startPoint: Int
    ): Response<MutableList<Article>>

    @GET(Constants.END_POINT_ARTICLES_COUNT)
    suspend fun getArticlesCount(): Response<Int>


    @GET(Constants.END_POINT_ARTICLES)
    suspend fun getSearchArticles(
        @Query("title_contains")
        searchQuery:String,
        @Query("_limit")
        limit: Int,
        @Query("_start")
        startPoint: Int
    ): Response<MutableList<Article>>

    @GET(Constants.END_POINT_ARTICLES_ID)
    suspend fun getArticle(@Path("id") id: Int): Response<List<Article>>



    /*
    in development

    @GET
    suspend fun getLaunch(id: Int)

    @GET
    suspend fun getEvent(id:Int)

    */
}