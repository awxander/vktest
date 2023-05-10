package com.example.vktest

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.vktest.di.AppModule
import com.example.vktest.di.ApplicationComponent
import com.example.vktest.di.DaggerApplicationComponent

class FilesWorkerApplication: Application() {

    lateinit var appComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)//выключил night mode
        appComponent = DaggerApplicationComponent
            .builder()
            .appModule(AppModule(applicationContext))
            .build()

    }
}

val Context.appComponent: ApplicationComponent
    get() = when (this) {
        is  FilesWorkerApplication -> appComponent
        else -> this.applicationContext.appComponent
    }