package com.example.plauenblod.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

fun initKoin(app: Application) {
    startKoin {
        androidContext(app)
        modules(appModule)
    }
}