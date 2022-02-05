package com.wojbeg.spacelaunchnews.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import com.wojbeg.spacelaunchnews.R
import com.wojbeg.spacelaunchnews.adapters.ArticleAdapter
import com.wojbeg.spacelaunchnews.databinding.FragmentSavedNewsBinding
import com.wojbeg.spacelaunchnews.ui.viewmodels.NewsViewModel
import com.wojbeg.spacelaunchnews.util.ClassTypes
import com.wojbeg.spacelaunchnews.util.OnClickTypesAdapterEnum
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {

    private val viewModel: NewsViewModel by activityViewModels()
    private lateinit var binding: FragmentSavedNewsBinding

    @Inject
    lateinit var articleAdapter: ArticleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSavedNewsBinding.bind(view)

        setupRecyclerView()

        articleAdapter.setOnItemClickListener {
            article, enum ->
            if(enum == OnClickTypesAdapterEnum.ArticleHolder){
                val bundle = Bundle().apply {
                    putSerializable("article", article)
                    putSerializable("favorite", true)
                }

                findNavController().navigate(
                    R.id.action_savedNewsFragment_to_articleFragment,
                    bundle
                )
            }else if(enum == OnClickTypesAdapterEnum.Event || enum == OnClickTypesAdapterEnum.Launch){
                val bundle = Bundle().apply {
                    putSerializable("article", article)
                }

                findNavController().navigate(
                    R.id.action_savedNewsFragment_to_articleReminderFragment,
                    bundle
                )

            }

        }

        val itemTouchHelperCallback = object: ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = articleAdapter.differ.currentList[position]


                viewModel.deleteArticle(article)

                Snackbar.make(view, getString(R.string.delete_article), Snackbar.LENGTH_LONG).apply {
                    setAction(getString(R.string.undo)){

                        viewModel.saveArticle(article)
                    }
                    show()
                }

            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvSavedNews)
        }

        viewModel.getSavedArticles().observe(viewLifecycleOwner, Observer{
            articles->
            articleAdapter.differ.submitList(articles)
        })

        viewModel.setPrevFragmentNum(ClassTypes.FAVOURITE)

    }

    private fun setupRecyclerView() =
        binding.rvSavedNews.apply {
        adapter = articleAdapter
        layoutManager = LinearLayoutManager(activity)
    }


}