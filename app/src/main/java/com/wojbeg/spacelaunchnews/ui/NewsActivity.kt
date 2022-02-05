package com.wojbeg.spacelaunchnews.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.wojbeg.spacelaunchnews.R
import com.wojbeg.spacelaunchnews.ui.viewmodels.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_news.*
import android.app.ActivityManager.TaskDescription
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration


@AndroidEntryPoint
class NewsActivity : AppCompatActivity() {

    val viewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_SpaceLaunchNews)
        setContentView(R.layout.activity_news)

        val navController = newsNavHostFragment.findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        bottomNavigationView.itemIconTintList = null
        bottomNavigationView.setupWithNavController(navController)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = newsNavHostFragment.findNavController()
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}