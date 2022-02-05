package com.wojbeg.spacelaunchnews.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.wojbeg.spacelaunchnews.models.Article


@Dao
@TypeConverters(Converters::class)
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: Article): Long

    @Delete
    suspend fun deleteArticle(article: Article)


    @Query("SELECT * FROM articles")
    fun getSavedArticles(): LiveData<List<Article>>

    @Query("SELECT COUNT(*) FROM articles")
    fun getArticlesCount(): Int

}