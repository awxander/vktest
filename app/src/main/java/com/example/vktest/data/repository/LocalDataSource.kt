package com.example.vktest.data.repository

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import com.example.vktest.domain.entites.FileInfo
import com.example.vktest.domain.repository.FilesRepository

class LocalDataSource(private val contentResolver: ContentResolver) : FilesRepository {


    override suspend fun loadFilesInfo(): List<FileInfo> {
        //TODO разбить на несколько функций, нечитаемое говно

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
                val name = cursor.getString(nameIndex) ?: continue
                val extension = name.split(".").let {
                    if (it.size < 2) {//если у нас при разбиении названия лишь
                        // 1 строка -> нет расширения -> это директория
                        "dir"
                    } else {
                        it[it.lastIndex]
                    }
                }
                val sizeInBytes = cursor.getLong(sizeIndex)
                val date = cursor.getLong(dateIndex)

                result.add(
                    FileInfo(
                        name = name,
                        sizeInBytes = sizeInBytes,
                        modifiedDateInSeconds = date,
                        extension = extension
                    )
                )

            } while (cursor.moveToNext())
        }
        return result
    }
}