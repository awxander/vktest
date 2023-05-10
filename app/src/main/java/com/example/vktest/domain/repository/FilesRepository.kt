package com.example.vktest.domain.repository

import android.net.Uri
import com.example.vktest.domain.entites.FileInfo

interface FilesRepository {
    suspend fun loadFilesInfo(uri : Uri?  = null) : List<FileInfo>
    suspend fun loadChangedFiles() : List<FileInfo>?

    suspend fun saveFilesHashesToDb()
}