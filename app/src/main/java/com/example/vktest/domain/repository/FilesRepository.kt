package com.example.vktest.domain.repository

import com.example.vktest.domain.entites.FileInfo

interface FilesRepository {
    suspend fun loadFilesInfo() : List<FileInfo>?
}