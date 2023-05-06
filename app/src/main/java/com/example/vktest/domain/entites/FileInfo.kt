package com.example.vktest.domain.entites

data class FileInfo(
    val name: String,
    val sizeInBytes: Long,
    val modifiedDateInSeconds: Long,
    val extension : String
)