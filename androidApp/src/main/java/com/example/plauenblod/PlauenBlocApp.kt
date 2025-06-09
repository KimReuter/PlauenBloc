package com.example.plauenblod

import android.app.Application
import com.example.plauenblod.di.initKoin

class PlauenBlocApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(this)
    }
}