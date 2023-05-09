package com.example.vktest.data.repository

import com.example.vktest.data.FileInfoConverter
import com.example.vktest.data.datasource.LocalDataSource
import com.example.vktest.data.db.FileHashDatabase
import com.example.vktest.domain.entites.FileInfo
import com.example.vktest.domain.repository.FilesRepository

class FilesRepositoryImpl(
    private val database: FileHashDatabase,
    private val dataSource: LocalDataSource
) : FilesRepository {

    private val changedFilesInfo = mutableListOf<FileInfo>()

    override suspend fun loadFilesInfo(): List<FileInfo> {
        val filesInfo = dataSource.loadFilesInfo()
        for(fileInfo in filesInfo){
            if(isChanged(fileInfo)){//обновляем измененные данные
                changedFilesInfo.add(fileInfo.copy())
            }
            saveFileHash(fileInfo)
        }

        return filesInfo
    }

    override suspend fun loadChangedFiles(): List<FileInfo> {
        return changedFilesInfo
    }

    private suspend fun saveFileHash(fileInfo: FileInfo){
        database.fileHashDao().insert(FileInfoConverter.toFileHashEntity(fileInfo))
    }


    private suspend fun isChanged(fileInfo : FileInfo) : Boolean{
        database.fileHashDao().loadFileHash(fileInfo.hashCode())?.hash ?: return true
        return false
    }
}