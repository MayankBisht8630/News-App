package com.mayankbisht.thenewsapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.mayankbisht.thenewsapp.R
import com.mayankbisht.thenewsapp.databinding.FragmentArticleBinding
import com.mayankbisht.thenewsapp.ui.NewsActivity
import com.mayankbisht.thenewsapp.ui.NewsViewModel

class ArticleFragment : Fragment(R.layout.fragment_article) {
    lateinit var newsViewModel: NewsViewModel
    lateinit var binding: FragmentArticleBinding

    val args: ArticleFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentArticleBinding.bind(view)

        newsViewModel = (activity as NewsActivity).newsViewModel
        var article = args.article

        binding.webView.apply {
            webViewClient = WebViewClient()
            article.url?.let {
                loadUrl(it)
            }
        }

        binding.fab.setOnClickListener{
            newsViewModel.addToFavourite(article)
            Snackbar.make(view,"Added to favourites",Snackbar.LENGTH_SHORT).show()
        }
    }
}