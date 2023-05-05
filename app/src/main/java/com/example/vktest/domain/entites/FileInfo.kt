package com.example.vktest.domain.entites

data class FileInfo(
    val name: String,
    val sizeInBytes: Long,
    val createDateInSeconds: Long,
    val extension : String
)