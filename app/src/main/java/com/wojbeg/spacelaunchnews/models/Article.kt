package com.wojbeg.spacelaunchnews.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.wojbeg.spacelaunchnews.db.Converters
import java.io.Serializable

@Entity(
    tableName = "articles"
)
@TypeConverters(Converters::class)
data class Article(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val title: String?,
    val url: String?,
    val imageUrl: String?,
    val newsSite: String?,
    val summary: String?,
    val publishedAt: String?,
    val updatedAt: String?,
    val featured: Boolean?,

    @Ignore
    val events: List<Event>?,
    @Ignore
    val launches: List<Launches>?
): Serializable{
    constructor(id: Int, title: String, url: String, imageUrl: String, newsSite: String, summary: String, publishedAt: String, updatedAt: String, featured: Boolean)
            : this(id, title, url, imageUrl, newsSite, summary, publishedAt, updatedAt, featured, emptyList(), emptyList())

    //Launches and events are not necessary to save in this stage of app
    //But will cause problems with room database

    //Will be added during application development (if it has any purpose)
    //TODO("Właściowści events oraz launches mogłyby mieć defaultowe wartość emptyList(). Wtedy dodatkowy konstruktor nie byłby potrzebny")
}