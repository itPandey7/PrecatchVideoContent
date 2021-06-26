package com.demo.precachingvideocontent.di.module

import android.content.Context
import com.demo.precachingvideocontent.utils.NetworkHelper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val appModule = module {
    single { provideNetworkHelper(androidContext()) }
}

private fun provideNetworkHelper(context: Context) = NetworkHelper(context)


