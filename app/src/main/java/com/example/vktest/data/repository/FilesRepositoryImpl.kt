package com.example.vktest.data.repository

import android.net.Uri
import com.example.vktest.data.FileInfoConverter
import com.example.vktest.data.datasource.LocalDataSource
import com.example.vktest.data.db.FileHashDatabase
import com.example.vktest.domain.entites.FileInfo
import com.example.vktest.domain.repository.FilesRepository

class FilesRepositoryImpl(
    private val database: FileHashDatabase,
    private val dataSource: LocalDataSource
) : FilesRepository {

    private var initialLoad = true

    private var filesInfo: List<FileInfo>? = null
    private val changedFilesInfo = mutableListOf<FileInfo>()
    private var lastSavedFileHashesInMap: HashMap<Int, Int>? = null

    override suspend fun loadFilesInfo(uri: Uri?): List<FileInfo> {
        filesInfo = if (uri == null)
            dataSource.loadDirectoryFilesInfo()
        else
            dataSource.loadDirectoryFilesInfo(uri)
        return filesInfo as List<FileInfo>
    }
    
    

    override suspend fun loadChangedFiles(): List<FileInfo> {
        return changedFilesInfo
    }

    override suspend fun saveFilesHashesToDb() {
        val filesInfo = dataSource.loadAllFilesInfo()
        lastSavedFileHashesInMap = loadFilesHashesFromDbInMap()
        for(fileInfo in filesInfo){
            if(!lastSavedFileHashesInMap!!.containsKey(fileInfo.hashCode())){
                database.fileHashDao().insert(FileInfoConverter.toFileHashEntity(fileInfo))
                fileInfo.modified = true
                changedFilesInfo.add(fileInfo)
            }
        }
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