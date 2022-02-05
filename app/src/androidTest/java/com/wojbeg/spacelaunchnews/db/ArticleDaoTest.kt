package com.wojbeg.spacelaunchnews.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.wojbeg.spacelaunchnews.getOrAwaitValue
import com.wojbeg.spacelaunchnews.models.Article
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

import org.junit.runner.RunWith


//@RunWith(AndroidJUnit4::class)
// to be replaced with hilt testing

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class ArticleDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: ArticleDatabase
    private lateinit var dao: ArticleDao


    @Before
    fun setup(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ArticleDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.getArticleDao()

    }

    @After
    fun teardown(){
        database.close()
    }

    @Test
    fun insertArticleItem() = runBlockingTest(){
        val article = Article(1140, "Dao Test",
            "https://testDao.pl", "https://testDaoPhoto.pl",
            "First Dao JUnit Test", "PublishedAtWebsite",
            "2021-11-02'22:06:59", "2021-11-02'22:07:00",
            false, listOf(), listOf())

        dao.insert(article)

        val allArticles = dao.getSavedArticles().getOrAwaitValue()

        assertThat(allArticles).contains(article)

    }


    @Test
    fun deleteArticleItem() = runBlockingTest {
        val article = Article(1140, "Dao Test",
            "https://testDao.pl", "https://testDaoPhoto.pl",
            "First Dao JUnit Test", "PublishedAtWebsite",
            "2021-11-02'22:06:59", "2021-11-02'22:07:00",
            false, listOf(), listOf())

        dao.insert(article)
        dao.deleteArticle(article)

        val allArticles = dao.getSavedArticles().getOrAwaitValue()

        assertThat(allArticles).doesNotContain(article)

    }

    @Test
    fun getAllArticles() = runBlockingTest {
        val article1 = Article(1145, "Dao Test2",
            "https://testDao1.pl", "https://testDaoPhoto.pl",
            "First Dao JUnit Test", "PublishedAtWebsite",
            "2021-11-02'22:06:59", "2021-11-02'22:07:00",
            false, listOf(), listOf())

        val article2 = Article(1146, "Dao Test2",
            "https://testDao2.pl", "https://testDaoPhoto.pl",
            "First Dao JUnit Test", "PublishedAtWebsite",
            "2021-11-02'22:06:59", "2021-11-02'22:07:00",
            false, listOf(), listOf())

        val article3 = Article(1147, "Dao Test3",
            "https://testDao3.pl", "https://testDaoPhoto.pl",
            "First Dao JUnit Test", "PublishedAtWebsite",
            "2021-11-02'22:06:59", "2021-11-02'22:07:00",
            false, listOf(), listOf())

        dao.insert(article1)
        dao.insert(article2)
        dao.insert(article3)

        assertThat(dao.getArticlesCount()).isEqualTo(3)

    }

}