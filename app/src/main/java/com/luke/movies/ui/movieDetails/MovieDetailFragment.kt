package com.luke.movies.ui.movieDetails

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.luke.movies.BuildConfig
import com.luke.movies.base.BaseFragment
import com.luke.movies.databinding.FragmentMovieDetailsBinding
import com.luke.movies.helpers.MovieState
import com.luke.movies.helpers.extensions.*
import com.luke.movies.model.movieDetails.MovieDetails
import com.luke.movies.viewModel.MoviesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 * Fragment for displaying the Movie Details by extending the BaseFragment
 */
class MovieDetailFragment : BaseFragment() {

    private val viewModel: MoviesViewModel by viewModel()
    private val binding by lazy{
        FragmentMovieDetailsBinding.inflate(layoutInflater)
    }
    private var movieId: Int = 0

    companion object {
        const val REQUEST_MOVIE_ID = "MovieId"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let { it.getInt(REQUEST_MOVIE_ID).let { id -> movieId = id } }
        observeData()
        return binding.root
    }

    /**
     * This function gathers the movie data from the server
     */
    private fun observeData() {
        viewModel.responseState.observe(viewLifecycleOwner, {movieState->
            when(movieState){
                is MovieState.LOADING -> {
                    binding.progressCircularDetails.visibility = if (movieState.isLoading) View.VISIBLE else View.GONE
                }
                is MovieState.SUCCESS->{
                    binding.progressCircularDetails.gone()
                    val response=movieState.response as MovieDetails
                    displayData(response)
                }
                is MovieState.ERROR->{
                    binding.error.visible()
                    binding.error.text = movieState.error.localizedMessage
                }
            }
        })
        viewModel.getMovieDetails(movieId)
    }

    private fun displayData(movieDetails: MovieDetails) {
        binding.title.text = movieDetails.title
        binding.releaseDate.text = movieDetails.releaseDate
        binding.description.text = movieDetails.overview
        binding.runtime.text = String.format("%s min", movieDetails.runtime)
        binding.image.loadImage("${BuildConfig.POSTER_BASE_URL}${movieDetails.posterPath}")
        binding.genre.text = movieDetails.genres.genreToCommaSeparatedString()
        binding.language.text = movieDetails.languages.languageToCommaSeparatedString()

        binding.bookNow.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("${BuildConfig.MOVIE_WEB_URL}${movieId}")
            startActivity(openURL)
        }
        updateTitle(movieDetails.title)
    }
}