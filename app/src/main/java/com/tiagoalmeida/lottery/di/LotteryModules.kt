package com.tiagoalmeida.lottery.di

import android.content.SharedPreferences
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tiagoalmeida.lottery.data.LotteryApi
import com.tiagoalmeida.lottery.data.repository.ConsultRepository
import com.tiagoalmeida.lottery.data.repository.ConsultRepositoryImpl
import com.tiagoalmeida.lottery.data.repository.PreferencesRepository
import com.tiagoalmeida.lottery.data.repository.PreferencesRepositoryImpl
import com.tiagoalmeida.lottery.data.source.ConsultDataSource
import com.tiagoalmeida.lottery.data.source.ConsultDataSourceImpl
import com.tiagoalmeida.lottery.domain.ConsultLatestResultsUseCase
import com.tiagoalmeida.lottery.domain.ConsultRangedResultsUseCase
import com.tiagoalmeida.lottery.ui.detail.DetailGameViewModel
import com.tiagoalmeida.lottery.ui.games.GamesViewModel
import com.tiagoalmeida.lottery.ui.main.MainViewModel
import com.tiagoalmeida.lottery.ui.register.GameRegisterViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LotteryModules {

    val firebase: Module = module {
        single { FirebaseCrashlytics.getInstance() }
        single { Firebase.analytics }
    }

    val dataSource: Module = module {
        single<ConsultDataSource> { ConsultDataSourceImpl(get()) }
    }

    val repository: Module = module {
        single<ConsultRepository> { ConsultRepositoryImpl(get()) }
    }

    val useCases: Module = module {
        single { ConsultLatestResultsUseCase(get()) }
        single { ConsultRangedResultsUseCase(get()) }
    }

    val viewModel: Module = module {
        viewModel { params -> DetailGameViewModel(get(), get(), get(), get()) }
        viewModel { GameRegisterViewModel(get(), get(), get()) }
        viewModel { MainViewModel(get(), get(), get(), get()) }
        viewModel { GamesViewModel(get(), get(), get()) }
    }

    val retrofit: Module = module {
        single { LotteryApi(createRetrofit()) }
    }

    fun sharedPreferences(preferences: SharedPreferences): Module = module {
        single<PreferencesRepository> { PreferencesRepositoryImpl(preferences, get()) }
    }

    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://apiloterias.com.br/app/")
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient
            .Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()
    }

}
