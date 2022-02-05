package com.wojbeg.spacelaunchnews.ui.fragments

import android.app.AlertDialog
import android.content.ClipData
import android.content.DialogInterface
import android.content.Intent
import android.net.http.SslError
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.wojbeg.spacelaunchnews.R
import com.wojbeg.spacelaunchnews.databinding.FragmentArticleBinding
import com.wojbeg.spacelaunchnews.ui.viewmodels.NewsViewModel
import com.wojbeg.spacelaunchnews.util.ClassTypes
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception

@AndroidEntryPoint
class ArticleFragment : Fragment(R.layout.fragment_article) {

    private val viewModel: NewsViewModel by activityViewModels()
    val args: ArticleFragmentArgs by navArgs()
    private lateinit var binding: FragmentArticleBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticleBinding.bind(view)

        val article = args.article
        val isFavorite = args.favorite

        binding.webView.apply {
            webViewClient = object : WebViewClient(){
                override fun onReceivedError(webView: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                    try {
                        webView?.stopLoading()
                    } catch (e: Exception) {
                    }

                    if (webView?.canGoBack() == true) {
                        webView.goBack();
                    }

                    webView?.loadUrl(getString(R.string.about_blank))

                    val alertDialog = AlertDialog.Builder(view.context)
                    .setTitle(getString(R.string.no_internet_info))
                    .setMessage(getString(R.string.no_internet_message))
                    .setPositiveButton("OK") {_, _ ->
                        activity?.onBackPressed();
                    }.setCancelable(false)
                        .create()

                    alertDialog.show()
                }

                override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                    handler!!.proceed()
                }
            }
            settings.apply {
                cacheMode = WebSettings.LOAD_DEFAULT
                javaScriptEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
            }

            article.url?.let {
                loadUrl(it)
            }
        }

        if(isFavorite){
            binding.fab.visibility = View.GONE
        }else{
            viewModel.getSavedArticles().observe(viewLifecycleOwner, Observer{
                    articles->
                if(articles.contains(article)){
                    binding.fab.visibility = View.GONE
                }
            })
        }

        binding.fab.setOnClickListener{
            viewModel.saveArticle(article)
            Snackbar.make(view, getString(R.string.article_save), Snackbar.LENGTH_LONG)
                .apply {
                    setAction(getString(R.string.undo)){
                        viewModel.deleteArticle(article)
                        binding.fab.visibility = View.VISIBLE
                    }
                    show()
                }
            binding.fab.visibility = View.GONE
        }

        viewModel.setPrevFragmentNum(ClassTypes.ARTICLE_DETAILS)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.bar_menu, menu)

        /*
        if(article.launches.isNullOrEmpty() && article.events.isNullOrEmpty()){
            menu.findItem(R.id.remind_menu).isVisible = false
        }
        */

        super.onCreateOptionsMenu(menu, inflater)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
        }else if(id == R.id.remind_menu){
            val bundle = Bundle().apply {
                putSerializable("article", args.article)
            }

            findNavController().navigate(
                R.id.action_articleFragment_to_articleReminderFragment,
                bundle
            )
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}