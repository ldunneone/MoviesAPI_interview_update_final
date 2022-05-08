package com.luke.movies.model.movieDetails

import com.google.gson.annotations.SerializedName

data class Language(
    @SerializedName("name")
    val name: String
)