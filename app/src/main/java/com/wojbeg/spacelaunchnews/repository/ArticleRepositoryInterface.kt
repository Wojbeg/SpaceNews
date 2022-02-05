package com.wojbeg.spacelaunchnews.repository

import androidx.lifecycle.LiveData
import com.wojbeg.spacelaunchnews.models.Article
import retrofit2.Response

interface ArticleRepositoryInterface {

    suspend fun getArticles(limit: Int, startPoint: Int): Response<MutableList<Article>>

    suspend fun getSearchArticles(searchQuery: String, limit: Int, startPoint: Int): Response<MutableList<Article>>

    suspend fun getArticlesCount(): Response<Int>

}