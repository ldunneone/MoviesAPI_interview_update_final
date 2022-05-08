package com.luke.movies.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.luke.movies.BuildConfig
import com.luke.movies.databinding.ItemMovieBinding
import com.luke.movies.helpers.extensions.loadImage
import com.luke.movies.model.movies.Movie

class MoviesPagedAdapter: PagingDataAdapter<Movie, RecyclerView.ViewHolder>(MovieDiffCallback()) {

    var onMovieClicked: ((Movie?) -> Unit)? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MovieItemViewHolder).bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MovieItemViewHolder(ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            (oldItem.id == newItem.id)

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            (oldItem == newItem)
    }

    inner class MovieItemViewHolder(view: ItemMovieBinding) : RecyclerView.ViewHolder(view.root) {
        val rootview:ItemMovieBinding=view
        fun bind(movie: Movie?) {
            rootview.title.text = movie?.title
            rootview.releaseDate.text = movie?.releaseDate
            val voteAverage: Double? = movie?.voteAverage
            rootview.circularProgressBar.progress = voteAverage?.times(10)?.toFloat()!!
            rootview.percentage.text = String.format("%d%s", voteAverage.times(10).toInt(), "%")
            rootview.image.loadImage("${BuildConfig.POSTER_BASE_URL}${movie.posterPath}")
            rootview.root.setOnClickListener {
                onMovieClicked?.invoke(movie)
            }
        }
    }
}