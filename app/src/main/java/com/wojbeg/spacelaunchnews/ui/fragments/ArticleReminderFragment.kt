package com.wojbeg.spacelaunchnews.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toolbar
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.RequestManager
import com.wojbeg.spacelaunchnews.R
import com.wojbeg.spacelaunchnews.ui.viewmodels.NewsViewModel
import com.wojbeg.spacelaunchnews.databinding.ArticleDetailsAndReminderBinding
import com.wojbeg.spacelaunchnews.models.Article
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.article_details_and_reminder.*
import javax.inject.Inject
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.wojbeg.spacelaunchnews.util.ClassTypes


@AndroidEntryPoint
class ArticleReminderFragment: Fragment(R.layout.article_details_and_reminder){

    private val viewModel: NewsViewModel by activityViewModels()
    private lateinit var binding: ArticleDetailsAndReminderBinding
    val args: ArticleFragmentArgs by navArgs()

    @Inject
    lateinit var glide: RequestManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ArticleDetailsAndReminderBinding.bind(view)

        val article = args.article

        glide
            .load(article.imageUrl)
            .into(binding.CollapsingImage)

        binding.apply {
            ToolbarTitle.title = article.newsSite
            ToolbarTitle.setTitleTextColor(resources.getColor(R.color.white))
            val white = ContextCompat.getColor(view.context, R.color.white)
            ToolbarTitle.setTitleTextColor(white)

            collapsingLayout.setCollapsedTitleTextColor(white)
            collapsingLayout.setExpandedTitleColor(white)
            titleText.text = article.title
            articleDescriptionReminder.text = article.summary
        }

        viewModel.setPrevFragmentNum(ClassTypes.REMINDER)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        showBackBtn()


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.bar_menu, menu)

        menu.findItem(R.id.remind_menu).isVisible = false

        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        var id = item.itemId

        if(id == R.id.share_menu){

            val sendIntent = Intent.createChooser(Intent().apply {

                val article = args.article

                action = Intent.ACTION_SEND

                putExtra(Intent.EXTRA_TEXT, article.summary+"\n"+getString(R.string.read_here)+"\n"+article.url+"\n\n"+getString(R.string.app_name))

                putExtra(Intent.EXTRA_SUBJECT, article.title)
                putExtra(Intent.EXTRA_TITLE, article.title)

                this.type = "text/plain"
            }, null)

            startActivity(sendIntent)

            return true
        }else if(id == android.R.id.home){

            val onBackPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    hideBackBtn()
                }
            }

            requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        }

        return super.onOptionsItemSelected(item)
    }

    private fun hideBackBtn(){
        val activity = (activity as AppCompatActivity)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        val actionBar = activity.actionBar
        actionBar?.setHomeButtonEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(false)
        actionBar?.setDisplayShowHomeEnabled(false)
    }

    private fun showBackBtn(){
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onDestroyView() {
        hideBackBtn()
        super.onDestroyView()
    }
}