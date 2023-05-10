package com.example.vktest.data

import com.example.vktest.data.db.FileHashEntity
import com.example.vktest.domain.entites.FileInfo

object FileInfoConverter {

    fun toFileHashEntity(from: FileInfo) =
        FileHashEntity(id = 0, hash = from.hashCode())
}