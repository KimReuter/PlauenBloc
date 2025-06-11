package com.example.plauenblod.android

import android.app.Application
import com.example.plauenblod.di.initKoin

class PlauenBlocApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(this)
    }
}