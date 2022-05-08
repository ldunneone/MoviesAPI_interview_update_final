package com.luke.movies.di

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.luke.movies.BuildConfig
import com.luke.movies.rest.api.ApiServices
import com.luke.movies.rest.repo.movies.MoviesRepository
import com.luke.movies.rest.repo.movies.MoviesRepositoryImpl
import com.luke.movies.viewModel.MoviesViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val repositoriesModule = module {

    fun provideMoviesRepository(apiServices: ApiServices): MoviesRepository =
        MoviesRepositoryImpl(apiServices)

    single { provideMoviesRepository(get()) }

}

val viewModelModule = module {

    fun provideMoviesViewModel(repository: MoviesRepository): MoviesViewModel =
        MoviesViewModel(repository)

    viewModel { provideMoviesViewModel(get()) }

}


val networkModules = module {

    single<ApiServices> {
        get<Retrofit>().create(ApiServices::class.java)
    }
    val connectTimeout: Long = 40
    val readTimeout: Long = 40

    fun provideHttpClient(): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .connectTimeout(connectTimeout, TimeUnit.SECONDS)
            .readTimeout(readTimeout, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            okHttpClientBuilder.addInterceptor(httpLoggingInterceptor)
        }
        okHttpClientBuilder.build()
        return okHttpClientBuilder.build()
    }

    fun provideRetrofit(client: OkHttpClient, baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()
    }

    single { provideHttpClient() }

    single {
        provideRetrofit(get(), BuildConfig.BASE_URL)
    }

}

val allModules = repositoriesModule + viewModelModule + networkModules