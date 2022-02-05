package com.wojbeg.spacelaunchnews.repository

import com.wojbeg.spacelaunchnews.db.ArticleDao
import com.wojbeg.spacelaunchnews.db.ArticleDatabase
import com.wojbeg.spacelaunchnews.models.Article
import javax.inject.Inject

class RoomRepository @Inject constructor (
    private val articleDatabase: ArticleDatabase
): RoomRepositoryInterface {

    private val articleDao: ArticleDao = articleDatabase.getArticleDao()

    override suspend fun insert(article: Article) =
        articleDao.insert(article)

    override fun getSavedNews() =
        articleDao.getSavedArticles()

    override suspend fun deleteArticle(article: Article) =
        articleDao.deleteArticle(article)


}