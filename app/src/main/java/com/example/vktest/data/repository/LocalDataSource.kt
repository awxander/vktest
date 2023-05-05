package com.example.vktest.data.repository

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.vktest.domain.entites.FileInfo
import com.example.vktest.domain.repository.FilesRepository

class LocalDataSource(private val contentResolver: ContentResolver) : FilesRepository{


   override suspend  fun loadFilesInfo() : List<FileInfo>{

        val result = mutableListOf<FileInfo>()

        val uri: Uri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.DATE_MODIFIED
        )
//        val selection = "${MediaStore.Files.FileColumns.DATA} like ?"
//        val selectionArgs = arrayOf("%/Documents/%")
        val sortOrder = "${MediaStore.Files.FileColumns.DISPLAY_NAME} ASC"

        val cursor = contentResolver
//            .query(uri, projection, selection, selectionArgs, sortOrder)
            .query(uri, projection, null, null, sortOrder)

        if (cursor != null && cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
            val dateIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)

            do {
                val name = cursor.getString(nameIndex)
                val extension = name?.split(".").let {
                    if (it == null || it.size < 2) {
                        "not declared"
                    } else {
                        it[it.lastIndex]
                    }
                }
                val sizeInBytes = cursor.getLong(sizeIndex)
                val date = cursor.getLong(dateIndex)

                result.add(FileInfo(
                    name = name,
                    sizeInBytes = sizeInBytes,
                    createDateInSeconds = date,
                    extension = extension
                ))

                Log.d(
                    "File Info",
                    "Name: $name, Size: $sizeInBytes bytes, extension: $extension , Date Modified: $date"
                )
            } while (cursor.moveToNext())
        }
       return result
    }
}