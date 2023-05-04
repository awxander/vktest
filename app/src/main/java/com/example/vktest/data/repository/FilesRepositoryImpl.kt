package com.example.vktest.data.repository

import com.example.vktest.domain.entites.FileInfo
import com.example.vktest.domain.repository.FilesRepository

class FilesRepositoryImpl : FilesRepository {
    override suspend fun loadFilesInfo(): List<FileInfo>? {
        TODO("Not yet implemented")
    }
}