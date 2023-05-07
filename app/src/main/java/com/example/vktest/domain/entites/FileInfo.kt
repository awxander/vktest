package com.example.vktest.domain.entites

import android.net.Uri

data class FileInfo(
    val uri : Uri,
    val name: String,
    val sizeInBytes: Long,
    val modifiedDateInSeconds: Long,
    val extension : String
)