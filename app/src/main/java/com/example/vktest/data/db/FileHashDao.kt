package com.example.vktest.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FileHashDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fileHashEntity: FileHashEntity)

    @Query("SELECT * FROM files_hash_table WHERE hash = :fileHash")
    suspend fun loadFileHash(fileHash: Int): FileHashEntity?
}