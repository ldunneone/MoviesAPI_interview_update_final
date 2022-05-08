package com.luke.movies.ui.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.luke.movies.R
import com.luke.movies.adapter.MoviesPagedAdapter
import com.luke.movies.base.BaseFragment
import com.luke.movies.databinding.FragmentMoviesBinding
import com.luke.movies.helpers.MovieState
import com.luke.movies.helpers.extensions.visible
import com.luke.movies.ui.movieDetails.MovieDetailFragment
import com.luke.movies.viewModel.MoviesViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

/** It will display the list movies in recyclerview
 */
class MoviesFragment : BaseFragment(){

    private val viewModel: MoviesViewModel by viewModel()
    private var movieAdapter = MoviesPagedAdapter()

    private val binding by lazy{
        FragmentMoviesBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        observeData()
        return binding.root
    }

    /**
     * It will observe the api call result and set the page data to adapter and also it update the ui state.
     */
    private fun observeData() {
        setUpRecyclerView()
        viewModel.moviePagedList.observe(viewLifecycleOwner, {
            lifecycleScope.launch {
                movieAdapter.submitData(it)
            }
        })
        viewModel.responseState.observe(viewLifecycleOwner, {movieState->
            when(movieState){
                is MovieState.LOADING -> {
                    binding.progressCircular.visibility = if (movieState.isLoading) View.VISIBLE else View.GONE
                }
                is MovieState.ERROR->{
                    binding.error.visible()
                    binding.error.text = movieState.error.localizedMessage
                }
            }
        })

        // If we need internet connection listener
        //enableInternetConnectionListener(this)

        enableHome(false)
    }

    private fun setUpRecyclerView() {
        binding.moviesRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            setHasFixedSize(true)
            adapter = movieAdapter
        }
        movieAdapter.onMovieClicked = {
            it?.let { movie ->
                openMovieDetails(movie.id)
            }
        }
    }

    private fun openMovieDetails(movieId: Int) {
        val bundle=Bundle().apply{
            putSerializable(MovieDetailFragment.REQUEST_MOVIE_ID,movieId)
        }
        findNavController().navigate(R.id.action_movieListFragment_to_movieDetailFragment,bundle)
    }
}