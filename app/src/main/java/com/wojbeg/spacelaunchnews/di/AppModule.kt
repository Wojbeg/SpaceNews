package com.wojbeg.spacelaunchnews.di

import android.content.Context
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.wojbeg.spacelaunchnews.api.ArticlesApi
import com.wojbeg.spacelaunchnews.db.ArticleDao
import com.wojbeg.spacelaunchnews.db.ArticleDatabase
import com.wojbeg.spacelaunchnews.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Singleton
    @Provides
    fun provideRetrofitInstance(): ArticlesApi =
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ArticlesApi::class.java)



    @Singleton
    @Provides
    fun provideArticleDao(database: ArticleDatabase): ArticleDao =
        database.getArticleDao()



    @Singleton
    @Provides
    fun provideAppDb(
        @ApplicationContext app: Context
    ): ArticleDatabase = Room.databaseBuilder(
        app,
        ArticleDatabase::class.java,
        "article_db"
    )
        .build()


    @Singleton
    @Provides
    fun provideGson() = Gson()


    @Singleton
    @Provides
    fun provideGlideInstance(
        @ApplicationContext context: Context
    ) = Glide.with(context).setDefaultRequestOptions(
        RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    )

}