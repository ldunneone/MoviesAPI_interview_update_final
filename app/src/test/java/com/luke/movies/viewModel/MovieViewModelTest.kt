package com.luke.movies.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.luke.movies.helpers.MovieState
import com.luke.movies.model.movieDetails.MovieDetails
import com.luke.movies.rest.repo.movies.MoviesRepository
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MovieViewModelTest {

    @get:Rule var rule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private val mockRepository = mockk<MoviesRepository>(relaxed = true)

    private lateinit var target: MoviesViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        target = MoviesViewModel(mockRepository)
    }

    @After
    fun shutdown() {
        clearAllMocks()
        Dispatchers.resetMain()
    }

    @Test
    fun `get play now movies when trying to load from server returns loading state`() {
        // Assign
        val stateList = mutableListOf<MovieState>()
        target.responseState.observeForever {
            stateList.add(it)
        }
        // Action
        target.getMovieDetails(1)

        // Assert
        assertThat(stateList).isNotEmpty()
        assertThat(stateList).hasSize(1)
        assertThat(stateList[0]).isInstanceOf(MovieState.LOADING::class.java)
    }

    @Test
    fun `get movie detail fun trying to load from server error state`() {
        // Assign
        every{ mockRepository.responseState} returns MutableStateFlow(MovieState.ERROR(Exception("Error")))
        val stateList = mutableListOf<MovieState>()
        target.responseState.observeForever {
            stateList.add(it)
        }

        // Action
        target.getMovieDetails(1)

        // Assertion
        assertThat(stateList).isNotEmpty()
        assertThat(stateList).hasSize(2)
        assertThat(stateList[0]).isInstanceOf(MovieState.LOADING::class.java)
        assertThat(stateList[1]).isInstanceOf(MovieState.ERROR::class.java)
    }

    @Test
    fun `get movie details when trying to load items from the server returns loading state`(){
        // Assign
        every{ mockRepository.responseState } returns MutableStateFlow(MovieState.LOADING())
        val stateList = mutableListOf<MovieState>()
        target.responseState.observeForever {
            stateList.add(it)
        }

        // Action
        target.getMovieDetails(1)

        // Assertion
        assertThat(stateList).isNotEmpty()
        assertThat(stateList).hasSize(2)
        assertThat(stateList[0]).isInstanceOf(MovieState.LOADING::class.java)
        assertThat(stateList[1]).isInstanceOf(MovieState.LOADING::class.java)
    }

    @Test
    fun `get movie details when trying to load items from the server returns success state`() {
        // Assign
        every{ mockRepository.responseState } returns MutableStateFlow(
            MovieState.SUCCESS(
                mockk<MovieDetails>()
            )
        )
        val stateList = mutableListOf<MovieState>()
        target.responseState.observeForever {
            stateList.add(it)
        }

        // Action
        target.getMovieDetails(1)

        // Assertion
        assertThat(stateList).isNotEmpty()
        assertThat(stateList).hasSize(2)
        assertThat(stateList[0]).isInstanceOf(MovieState.LOADING::class.java)
        assertThat(stateList[1]).isInstanceOf(MovieState.SUCCESS::class.java)
    }
}