package com.sangmeebee.searchmovie.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sangmeebee.searchmovie.R
import com.sangmeebee.searchmovie.databinding.ActivityMainBinding
import com.sangmeebee.searchmovie.model.UIState
import com.sangmeebee.searchmovie.ui.customException.EmptyQueryException
import com.sangmeebee.searchmovie.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModels<MainViewModel>()
    private val movieAdapter: MovieAdapter = MovieAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            this.lifecycleOwner = this@MainActivity
            this.viewModel = mainViewModel
        }
        setContentView(binding.root)

        setRecyclerView()
        observeUIState()
    }

    private fun observeUIState() = repeatOnStarted {
        mainViewModel.uiState.collect { uiState ->
            when (uiState) {
                is UIState.Empty -> {

                }
                is UIState.Loading -> {

                }
                is UIState.Success -> {
                    movieAdapter.submitList(uiState.data.items)
                }
                is UIState.Error -> {
                    when (uiState.throwable) {
                        is EmptyQueryException -> showToast(resources.getString(R.string.movie_list_empty_query))
                        else -> showToast(uiState.throwable.message)
                    }
                }
            }
        }
    }

    private fun setRecyclerView() {
        binding.rvMovieList.adapter = movieAdapter
    }

    private fun showToast(message: String?) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}