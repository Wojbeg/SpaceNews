package com.wojbeg.spacelaunchnews.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wojbeg.spacelaunchnews.R
import com.wojbeg.spacelaunchnews.adapters.ArticleAdapter
import com.wojbeg.spacelaunchnews.databinding.FragmentLaunchNewsBinding
import com.wojbeg.spacelaunchnews.models.Article
import com.wojbeg.spacelaunchnews.ui.viewmodels.NewsViewModel
import com.wojbeg.spacelaunchnews.util.ClassTypes
import com.wojbeg.spacelaunchnews.util.Constants
import com.wojbeg.spacelaunchnews.util.OnClickTypesAdapterEnum
import com.wojbeg.spacelaunchnews.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LaunchNewsFragment : Fragment(R.layout.fragment_launch_news) {

    private val viewModel: NewsViewModel by activityViewModels()
    private lateinit var binding: FragmentLaunchNewsBinding

    @Inject
    lateinit var articleAdapter: ArticleAdapter

    var isLoading = false
    var isLastPage = false
    var isScrolling = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLaunchNewsBinding.bind(view)

        setupRecyclerView()

        articleAdapter.setOnItemClickListener {
                article, enum ->
            if(enum == OnClickTypesAdapterEnum.ArticleHolder){
                val bundle = Bundle().apply {
                    putSerializable("article", article)
                }

                findNavController().navigate(
                    R.id.action_launchNewsFragment_to_articleFragment,
                    bundle
                )
            }else {
                val bundle = Bundle().apply {
                    putSerializable("article", article)
                }

                findNavController().navigate(
                    R.id.action_launchNewsFragment_to_articleReminderFragment,
                    bundle
                )

            }
        }


        viewModel.reportsLiveData.observe(viewLifecycleOwner, Observer {

            response ->
            when(response) {
                is Resource.Success -> handleSuccess(response)
                is Resource.Error -> handleError(response)
                is Resource.Loading -> showProgressBar()

            }
        })

        binding.refreshLayout.setOnRefreshListener {
            viewModel.clearArticles()
            articleAdapter.notifyDataSetChanged()
            viewModel.refreshArticles()
            binding.refreshLayout.isRefreshing = false
        }


        if(viewModel.getPrevFragmentNum() == ClassTypes.SEARCH){
            viewModel.clearArticles()
            articleAdapter.notifyDataSetChanged()
            viewModel.refreshArticles()
        }

        viewModel.setPrevFragmentNum(ClassTypes.HOME)

    }


    private fun setupRecyclerView() =
        binding.rvBreakingNews.apply {
        adapter = articleAdapter
        layoutManager = LinearLayoutManager(activity)
        addOnScrollListener(newsScrollListener)

    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private val newsScrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE

            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem
                    && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if(shouldPaginate){

                viewModel.getArticles()
                isScrolling = false
            }
        }
    }

    private fun handleSuccess(responseSuccess: Resource<MutableList<Article>>){
        hideProgressBar()
        responseSuccess.data?.let {
                articleResponse ->
            articleAdapter.differ.submitList(articleResponse)

            articleAdapter.notifyDataSetChanged()

            isLastPage = viewModel.isLastPage()

            if(isLastPage){
                binding.rvBreakingNews.setPadding(0,0,0,0)
            }

        }
    }

    private fun handleError(responseError: Resource<MutableList<Article>>){
        hideProgressBar()
        responseError.message?.let {
            Toast.makeText(activity, getString(R.string.error) + it, Toast.LENGTH_LONG).show()
        }
    }

}