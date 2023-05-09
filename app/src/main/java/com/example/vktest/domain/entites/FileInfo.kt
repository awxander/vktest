package com.example.vktest.domain.entites

import android.net.Uri
import java.util.*

data class FileInfo(
    val uri : Uri,
    val name: String,
    val sizeInBytes: Long,
    val modifiedDateInSeconds: Long,
    val extension : String
){
    override fun hashCode(): Int {
        return Objects.hash(name, sizeInBytes, modifiedDateInSeconds)
    }
}