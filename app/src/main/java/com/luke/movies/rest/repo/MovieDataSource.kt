package com.luke.movies.rest.repo.movies

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingState
import androidx.paging.rxjava2.RxPagingSource
import com.luke.movies.di.FIRST_PAGE
import com.luke.movies.helpers.MovieState
import com.luke.movies.rest.api.ApiServices
import com.luke.movies.model.movies.Movie
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

class MovieDataSource(
    private val apiService: ApiServices
) : RxPagingSource<Int, Movie>() {

    val _responseState: MutableLiveData<MovieState> =MutableLiveData(MovieState.LOADING())

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { state.closestItemToPosition(it)?.id }
    }

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, Movie>> {
        _responseState.postValue(MovieState.LOADING())
        val page = params.key ?: FIRST_PAGE
        return apiService.getMovies(page)
            .subscribeOn(Schedulers.io())
            .map <LoadResult<Int, Movie>> {response ->
                _responseState.postValue(MovieState.LOADING(false))
                response.movies.let{ list ->
                    LoadResult.Page(
                        list,
                        prevKey = if (page == FIRST_PAGE) null else page - 1,
                        nextKey = if (list.isEmpty()) null else page + 1
                    )
                }
            }.onErrorReturn { e ->
                when (e) {
                    is IOException -> {
                        _responseState.postValue(MovieState.ERROR(e))
                        LoadResult.Error(e)
                    }
                    is HttpException -> {
                        _responseState.postValue(MovieState.ERROR(e))
                        LoadResult.Error(e)
                    }
                    else -> {
                        _responseState.postValue(MovieState.ERROR(e))
                        LoadResult.Error(e)
                    }
                }
            }
    }
}