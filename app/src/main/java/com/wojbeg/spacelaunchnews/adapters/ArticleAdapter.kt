package com.wojbeg.spacelaunchnews.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.wojbeg.spacelaunchnews.R
import com.wojbeg.spacelaunchnews.databinding.ArticlePreviewBinding
import com.wojbeg.spacelaunchnews.models.Article
import java.text.SimpleDateFormat
import javax.inject.Inject
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.wojbeg.spacelaunchnews.util.OnClickTypesAdapterEnum

class ArticleAdapter @Inject constructor(
  private val glide: RequestManager
) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

    }

    val differ = AsyncListDiffer(this, diffCallback)

    inner class ArticleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val binding = ArticlePreviewBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder{

        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.article_preview,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Article, OnClickTypesAdapterEnum) -> Unit)? = null

    //TODO("Inny sposób na parsowanie")
    private val inputDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    private val outputDateFormat: SimpleDateFormat = SimpleDateFormat("HH:mm dd.MM.yyyy")

    override fun onBindViewHolder(holder: ArticleAdapter.ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]

        holder.itemView.apply {

            holder.binding.apply {
                tvTitle.text = article.title
                tvDescription.text = article.summary
                tvPublishedAt.text = outputDateFormat.format(inputDateFormat.parse(article.publishedAt))
                tvSource.text = article.newsSite

                flightEventsDrawable.visibility = View.GONE
                flightLaunchDrawable.visibility = View.GONE

                val areArticles = !article.events.isNullOrEmpty()
                val areEvents = !article.launches.isNullOrEmpty()

                if (areArticles){
                    flightEventsDrawable.visibility = View.VISIBLE
                    flightEventsDrawable.setOnClickListener {
                        onItemClickListener?.let { it(article, OnClickTypesAdapterEnum.Event) }
                    }

                }

                if(areEvents){
                    flightLaunchDrawable.visibility = View.VISIBLE
                    flightLaunchDrawable.setOnClickListener{
                        onItemClickListener?.let { it(article, OnClickTypesAdapterEnum.Launch) }
                    }
                }

                if(!areArticles && !areEvents){
                    newsDrawable.visibility = View.VISIBLE
                    newsDrawable.setOnClickListener {
                        onItemClickListener?.let { it(article, OnClickTypesAdapterEnum.Article) }
                    }
                }
            }

            val circularProgressDrawable = CircularProgressDrawable(context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()

            glide
                .load(article.imageUrl)
                .placeholder(circularProgressDrawable)
                .into(holder.binding.ivArticleImage)



            setOnClickListener{
                onItemClickListener?.let { it(article, OnClickTypesAdapterEnum.ArticleHolder) }
            }

        }

    }

    fun setOnItemClickListener(listener: (Article, OnClickTypesAdapterEnum) -> Unit){
        onItemClickListener = listener
    }

}