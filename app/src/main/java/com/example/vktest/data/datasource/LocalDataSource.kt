package com.example.vktest.data.datasource

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import com.example.vktest.Extensions
import com.example.vktest.domain.entites.FileInfo
import com.example.vktest.domain.repository.FilesRepository

class LocalDataSource(private val contentResolver: ContentResolver) {

    suspend fun loadFilesInfo(
        dirUri: Uri?
    ): List<FileInfo> {
        val result = mutableListOf<FileInfo>()
        if (dirUri != null) {

        } else {
            val uri: Uri = MediaStore.Files.getContentUri("downloads")
            val projection = arrayOf(
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                MediaStore.Files.FileColumns._ID
            )
            val sortOrder = "${MediaStore.Files.FileColumns.DISPLAY_NAME} ASC"

            val cursor = contentResolver
                .query(uri, projection, null, null, sortOrder)

            if (cursor != null && cursor.moveToFirst()) {
                val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                val nameIndex =
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
                val dateIndex =
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)

                do {
                    val id = cursor.getLong(idIndex)
                    val name = cursor.getString(nameIndex) ?: continue
                    val extension = name.split(".").let {
                        if (it.size < 2) {//если у нас при разбиении названия лишь
                            // 1 строка -> нет расширения -> это директория
                            Extensions.DIRECTORY
                        } else {
                            it[it.lastIndex]
                        }
                    }
                    val sizeInBytes = cursor.getLong(sizeIndex)
                    val date = cursor.getLong(dateIndex)
                    val fileUri = ContentUris.withAppendedId(uri, id)

                    result.add(
                        FileInfo(
                            name = name,
                            sizeInBytes = sizeInBytes,
                            modifiedDateInSeconds = date,
                            extension = extension,
                            uri = fileUri
                        )
                    )
                } while (cursor.moveToNext())
            }
            cursor?.close()
        }
        return result
    }

    fun getDirectoryFilesInfo() {

    }
}