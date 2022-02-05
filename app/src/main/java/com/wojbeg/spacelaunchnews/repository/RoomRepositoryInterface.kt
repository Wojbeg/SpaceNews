package com.wojbeg.spacelaunchnews.repository

import androidx.lifecycle.LiveData
import com.wojbeg.spacelaunchnews.db.ArticleDao
import com.wojbeg.spacelaunchnews.models.Article

interface RoomRepositoryInterface {


    suspend fun insert(article: Article): Long

    fun getSavedNews() : LiveData<List<Article>>

    suspend fun deleteArticle(article: Article)

}