package com.example.vktest.data.repository

import android.net.Uri
import com.example.vktest.data.FileInfoConverter
import com.example.vktest.data.datasource.LocalDataSource
import com.example.vktest.data.db.FileHashDatabase
import com.example.vktest.domain.entites.FileInfo
import com.example.vktest.domain.repository.FilesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FilesRepositoryImpl(
    private val database: FileHashDatabase,
    private val dataSource: LocalDataSource
) : FilesRepository {

    private var initialLoad = true

    private var filesInfo: List<FileInfo>? = null
    private val changedFilesInfo = mutableListOf<FileInfo>()
    private var fileHashesInMap: HashMap<Int, Int>? = null

    override suspend fun loadFilesInfo(uri: Uri?): List<FileInfo> {
        filesInfo = if (uri == null)
            dataSource.loadFilesInfo()
        else
            dataSource.loadFilesInfo(uri)

        if (initialLoad) {
            CoroutineScope(Dispatchers.Default).launch {
                fileHashesInMap = loadFilesHashesFromDbInMap()
                for (fileInfo in filesInfo!!) {
                    if (!fileHashesInMap!!.containsKey(fileInfo.hashCode())) {
                        fileInfo.modified = true
                        changedFilesInfo.add(fileInfo)//добавляем измененный файл
                        database.fileHashDao()
                            .insert(FileInfoConverter.toFileHashEntity(fileInfo))//добавляем его хэш в бд
                    }
                }
            }
            initialLoad = false
        }
        return filesInfo as List<FileInfo>
    }

    override suspend fun loadChangedFiles(): List<FileInfo> {
        return changedFilesInfo
    }

    private suspend fun loadFilesHashesFromDbInMap(): HashMap<Int, Int> {
        val fileHashesMap = HashMap<Int, Int>()
        val fileHashes = database.fileHashDao().loadAllFileHashes()
        for (fileHash in fileHashes) {
            fileHashesMap[fileHash.hash] = fileHash.id
        }
        return fileHashesMap
    }

}