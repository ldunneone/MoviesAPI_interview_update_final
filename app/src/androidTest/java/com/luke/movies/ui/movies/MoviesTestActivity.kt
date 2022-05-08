package com.luke.movies.ui.movies

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.agoda.kakao.screen.Screen
import com.luke.movies.screen.TestMoviesScreen
import com.luke.movies.ui.MoviesActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class TestMoviesActivityTest {
    @Rule
    @JvmField
    val rule = ActivityScenarioRule(MoviesActivity::class.java)

    @Test
    fun shouldShowContents_whenLaunchingMoviesActivity() {
        Screen.onScreen<TestMoviesScreen> {
            content {
                isVisible()
            }
        }
    }


    @Test
    fun testContentItemsRecyclerView() {
        Screen.onScreen<TestMoviesScreen> {
            moviesRecyclerView {
                isVisible()
            }
        }
    }
}
