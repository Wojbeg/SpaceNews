package com.wojbeg.spacelaunchnews.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.wojbeg.spacelaunchnews.R
import com.wojbeg.spacelaunchnews.adapters.ArticleAdapter
import com.wojbeg.spacelaunchnews.databinding.FragmentSearchArticlesBinding
import com.wojbeg.spacelaunchnews.ui.viewmodels.NewsViewModel
import com.wojbeg.spacelaunchnews.util.Constants
import com.wojbeg.spacelaunchnews.util.OnClickTypesAdapterEnum
import com.wojbeg.spacelaunchnews.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.app.Activity
import android.view.inputmethod.InputMethodManager
import com.wojbeg.spacelaunchnews.util.ClassTypes


@AndroidEntryPoint
class SearchArticlesFragment : Fragment(R.layout.fragment_search_articles)  {

    private val viewModel: NewsViewModel by activityViewModels()
    private lateinit var binding: FragmentSearchArticlesBinding

    @Inject
    lateinit var searchAdapter: ArticleAdapter

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private var wasFound = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchArticlesBinding.bind(view)

        //TODO("Add search filters")
        //TODO("Repair http time exception")

        setupRecyclerView()

        searchAdapter.setOnItemClickListener {
                article, enum ->
            if(enum == OnClickTypesAdapterEnum.ArticleHolder){
                val bundle = Bundle().apply {
                    putSerializable("article", article)
                }

                findNavController().navigate(
                    R.id.action_searchArticlesFragment_to_articleFragment,
                    bundle
                )
            }else if(enum == OnClickTypesAdapterEnum.Event || enum == OnClickTypesAdapterEnum.Launch){
                val bundle = Bundle().apply {
                    putSerializable("article", article)
                }

                findNavController().navigate(
                    R.id.action_searchArticlesFragment_to_articleReminderFragment,
                    bundle
                )

            }

        }

        binding.etSearch.isCursorVisible = false

        binding.etSearch.setOnClickListener { view ->
            binding.etSearch.apply {
                isCursorVisible = !isCursorVisible
            }

        }

        var job: Job? = null
        binding.etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(Constants.SEARCH_NEWS_DELAY)
                editable?.let {
                    if(it.trim().isNotBlank()){
                        wasFound = false
                        viewModel.clearSearch()
                        searchAdapter.notifyDataSetChanged()
                        viewModel.getSearchNews(it.toString().trim())
                    }else{
                        Snackbar.make(view, getString(R.string.empty), Snackbar.LENGTH_LONG).show()
                    }

                    binding.etSearch.clearFocus()
                    binding.etSearch.isCursorVisible = false

                    val inputMethodManager: InputMethodManager =
                        requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        }

        viewModel.searchLiveData.observe(viewLifecycleOwner, Observer { response->

            when(response){
                is Resource.Success -> {

                    hideProgressBar()
                    response.data?.let {
                        searchResponse ->
                        searchAdapter.differ.submitList(searchResponse)
                        searchAdapter.notifyDataSetChanged()

                        isLastPage = searchResponse.isEmpty()

                        if(!wasFound && !isLastPage){
                            wasFound = !wasFound
                        }else if(!wasFound && isLastPage){
                            Snackbar.make(view, getString(R.string.not_found_snackbar), Snackbar.LENGTH_LONG).show()
                        }else if(wasFound && isLastPage){
                            Snackbar.make(view, getString(R.string.thats_all), Snackbar.LENGTH_LONG).show()
                        }

                        if(isLastPage){
                            binding.rvSearchNews.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let {
                        Toast.makeText(activity, getString(R.string.error)+it, Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }

            }

        })

        viewModel.setPrevFragmentNum(ClassTypes.SEARCH)

        //initialization with articles from launchNews


    }


    private fun setupRecyclerView() =
        binding.rvSearchNews.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(searchNewsScrollListener)

        }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private val searchNewsScrollListener = object: RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
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
                viewModel.getSearchNews(binding.etSearch.text.toString().trim())
                isScrolling = false
            }

        }
    }


}