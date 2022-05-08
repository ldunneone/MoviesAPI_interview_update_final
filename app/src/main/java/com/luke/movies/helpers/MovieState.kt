package com.luke.movies.helpers

sealed class MovieState{
    class LOADING(val isLoading:Boolean=true) : MovieState()
    class SUCCESS(val response: Any): MovieState()
    class ERROR(val error:Throwable): MovieState()
}