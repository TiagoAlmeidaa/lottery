package com.tiagoalmeida.lottery.di

import android.content.SharedPreferences
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tiagoalmeida.lottery.model.mapper.LotteryResultParser
import com.tiagoalmeida.lottery.network.AppRetrofit
import com.tiagoalmeida.lottery.network.datasource.ConsultDataSource
import com.tiagoalmeida.lottery.network.datasource.ConsultDataSourceImpl
import com.tiagoalmeida.lottery.network.repository.ConsultRepository
import com.tiagoalmeida.lottery.network.repository.ConsultRepositoryImpl
import com.tiagoalmeida.lottery.network.repository.PreferencesRepository
import com.tiagoalmeida.lottery.network.repository.PreferencesRepositoryImpl
import com.tiagoalmeida.lottery.viewmodel.MainViewModel
import com.tiagoalmeida.lottery.viewmodel.detail.DetailGameViewModel
import com.tiagoalmeida.lottery.viewmodel.games.GamesViewModel
import com.tiagoalmeida.lottery.viewmodel.register.game.GameRegisterViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object LotteryModules {

    val firebase: Module = module {
        single { FirebaseCrashlytics.getInstance() }
    }

    val parsers: Module = module {
        single { LotteryResultParser() }
    }

    val dataSource: Module = module {
        single<ConsultDataSource> { ConsultDataSourceImpl(get()) }
    }

    val repository: Module = module {
        single<ConsultRepository> { ConsultRepositoryImpl(get(), get()) }
    }

    val viewModel: Module = module {
        viewModel { params -> DetailGameViewModel(get(), params.get(), get()) }
        viewModel { GameRegisterViewModel(get(), get()) }
        viewModel { MainViewModel(get(), get(), get()) }
        viewModel { GamesViewModel(get(), get()) }
    }

    val retrofit: Module = module {
        single { AppRetrofit(createRetrofit()) }
    }

    fun sharedPreferences(preferences: SharedPreferences): Module = module {
        single<PreferencesRepository> { PreferencesRepositoryImpl(preferences, get()) }
    }

    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://apiloterias.com.br/app/")
            .client(createOkHttpClient())
            .addConverterFactory(ScalarsConverterFactory.create())
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
