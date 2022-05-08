package com.luke.movies.rest.repo.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.luke.movies.di.MOVIES_PER_PAGE
import com.luke.movies.helpers.MovieState
import com.luke.movies.model.movies.Movie
import com.luke.movies.rest.api.ApiServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface MoviesRepository{
    val responseState: StateFlow<MovieState>
    fun removeObserver()
    fun fetchLiveMoviePagedList(): LiveData<PagingData<Movie>>
    fun getMovieDetails(movieId: Int,compositeDisposable:CompositeDisposable)
}

class MoviesRepositoryImpl(private val apiService: ApiServices):MoviesRepository {

    private var _responseState: MutableStateFlow<MovieState> = MutableStateFlow(MovieState.LOADING())
    override val responseState: StateFlow<MovieState> = _responseState

    private lateinit var dataSource:MovieDataSource

    override fun fetchLiveMoviePagedList(): LiveData<PagingData<Movie>> {
        dataSource= MovieDataSource(apiService)
        dataSource._responseState.observeForever(movieStateObserver)
        return Pager(
            config = PagingConfig(pageSize = MOVIES_PER_PAGE, enablePlaceholders = true),
            pagingSourceFactory = {dataSource}
        ).liveData
    }

    private val movieStateObserver = Observer<MovieState> { movieState ->
        _responseState.value=(movieState)
    }

    override fun getMovieDetails(movieId: Int,compositeDisposable:CompositeDisposable) {
        _responseState.value=(MovieState.LOADING())
        try {
            compositeDisposable.add(
            apiService.getMovieDetails(movieId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    _responseState.value=(MovieState.SUCCESS(it))
                }, {
                    _responseState.value=(MovieState.ERROR(it))
                }))
        } catch (e: Exception) {
            _responseState.value=(MovieState.ERROR(e))
        }
    }

    override fun removeObserver(){
        dataSource._responseState.removeObserver(movieStateObserver)
    }
}