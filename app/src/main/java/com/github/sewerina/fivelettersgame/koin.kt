package com.github.sewerina.fivelettersgame

import com.github.sewerina.fivelettersgame.api.ApiRepository
import com.github.sewerina.fivelettersgame.api.ApiRepositoryImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<ApiRepository> { ApiRepositoryImpl() }
    viewModel { MainViewModel(get()) }
}