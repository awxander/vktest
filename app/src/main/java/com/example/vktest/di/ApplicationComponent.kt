package com.example.vktest.di

import com.example.vktest.MainActivity
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class])
interface ApplicationComponent {

    fun inject(mainActivity: MainActivity)
}

