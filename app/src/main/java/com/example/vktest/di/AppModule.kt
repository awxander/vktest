package com.example.vktest.di

import android.content.ContentResolver
import android.content.Context
import com.example.vktest.data.datasource.LocalDataSource
import com.example.vktest.data.db.FileHashDatabase
import com.example.vktest.data.repository.FilesRepositoryImpl
import com.example.vktest.domain.repository.FilesRepository
import com.example.vktest.presentation.FilesViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class AppModule(private val context: Context) {

    @Singleton
    @Provides
    fun provideFilesViewModel(filesRepository: FilesRepository) : FilesViewModel{
        return FilesViewModel(filesRepository)
    }
    @Singleton
    @Provides
    fun provideFileRepository(filesRepositoryImpl: FilesRepositoryImpl): FilesRepository {
        return filesRepositoryImpl
    }
    @Singleton
    @Provides
    fun provideFileRepositoryImpl(
        database: FileHashDatabase,
        dataSource: LocalDataSource
    ): FilesRepositoryImpl {
        return FilesRepositoryImpl(
            database = database,
            dataSource = dataSource
        )
    }
    @Singleton
    @Provides
    fun provideDatabase() : FileHashDatabase{
        return FileHashDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideLocalDataSource(contentResolver : ContentResolver) : LocalDataSource{
        return LocalDataSource(contentResolver)
    }

    @Provides
    fun provideContentResolver() : ContentResolver{
        return context.contentResolver
    }


}