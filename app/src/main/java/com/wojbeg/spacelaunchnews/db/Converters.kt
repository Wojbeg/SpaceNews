package com.wojbeg.spacelaunchnews.db

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wojbeg.spacelaunchnews.models.Article
import com.wojbeg.spacelaunchnews.models.Event
import com.wojbeg.spacelaunchnews.models.Launches
import javax.inject.Inject

@ProvidedTypeConverter
class Converters {

    @Inject
    lateinit var gson: Gson


    @TypeConverter
    fun toArticle(provider: String): Article{
        return gson.fromJson(provider, Article::class.java)
    }

    @TypeConverter
    fun fromArticle(provider: Article): String{
        return gson.toJson(provider)
    }

}