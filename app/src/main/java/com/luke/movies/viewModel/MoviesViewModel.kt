package com.luke.movies.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.luke.movies.helpers.MovieState
import com.luke.movies.model.movies.Movie
import com.luke.movies.rest.repo.movies.MoviesRepository
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.*

/**
 * This is a universal ViewModel that is used for all fragments
 */
class MoviesViewModel(private val movieRepository: MoviesRepository,
                      private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
                      private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + ioDispatcher)
) : CoroutineScope by coroutineScope, ViewModel(){

    /**
     * This is the LiveData calling the MovieState when the launch scope coroutine is activated
     */
    private var _responseState:MutableLiveData<MovieState> = MutableLiveData(MovieState.LOADING())
    val responseState:LiveData<MovieState> = _responseState

    private fun collectState(){
        launch {
            movieRepository.responseState.collect {movieState->
                _responseState.postValue(movieState)
            }
        }
    }

    private val compositeDisposable = CompositeDisposable()

    val moviePagedList: LiveData<PagingData<Movie>> by lazy {
        collectState()
        movieRepository.fetchLiveMoviePagedList()
    }

    fun getMovieDetails(movieId: Int) {
        collectState()
        movieRepository.getMovieDetails(movieId, compositeDisposable)
    }

    override fun onCleared() {
        movieRepository.removeObserver()
        compositeDisposable.dispose()
        super.onCleared()
    }
}