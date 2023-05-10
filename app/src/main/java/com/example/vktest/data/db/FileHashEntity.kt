package com.example.vktest.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "files_hash_table")
data class FileHashEntity(
@PrimaryKey(autoGenerate = true)
val id : Int,
val hash : Int
)
