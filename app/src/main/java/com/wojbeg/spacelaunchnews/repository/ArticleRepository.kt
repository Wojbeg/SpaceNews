package com.wojbeg.spacelaunchnews.repository

import com.wojbeg.spacelaunchnews.api.ArticlesApi
import com.wojbeg.spacelaunchnews.db.ArticleDao
import com.wojbeg.spacelaunchnews.db.ArticleDatabase
import com.wojbeg.spacelaunchnews.models.Article
import com.wojbeg.spacelaunchnews.ui.NewsActivity
import retrofit2.Response
import retrofit2.Retrofit

import javax.inject.Inject

class ArticleRepository @Inject constructor (
    private val retrofitInstance: ArticlesApi,
): ArticleRepositoryInterface {

    override suspend fun getArticles(limit: Int, startPoint: Int): Response<MutableList<Article>> {
        return retrofitInstance.getArticles(limit, startPoint)
    }

    override suspend fun getSearchArticles(searchQuery: String, limit: Int, startPoint: Int): Response<MutableList<Article>>{
       return retrofitInstance.getSearchArticles(searchQuery, limit, startPoint)
    }

    override suspend fun getArticlesCount(): Response<Int> {
        return retrofitInstance.getArticlesCount()
    }
}