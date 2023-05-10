package com.example.vktest.data.datasource

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.example.vktest.Extensions
import com.example.vktest.domain.entites.FileInfo
import java.io.File

class LocalDataSource(private val contentResolver: ContentResolver) {

    suspend fun loadDirectoryFilesInfo(dirUri: Uri? = null): List<FileInfo> {
        val filesInfo = mutableListOf<FileInfo>()


        val root = if (dirUri != null) {
            File(dirUri.path)
        } else {
            Environment.getExternalStorageDirectory()
        }

        if (root.isDirectory) {
            val filesList = root.listFiles() ?: return emptyList()
            for (file in filesList) {
                val name = file.name
                val sizeInBytes = file.length()
                val extension = if (file.isDirectory)
                    Extensions.DIRECTORY
                else
                    file.extension
                val uri = Uri.parse(file.toURI().toString())
                val modifiedDateInSeconds = file.lastModified() / 1000
                filesInfo.add(
                    FileInfo(
                        name = name,
                        sizeInBytes = sizeInBytes,
                        modifiedDateInSeconds = modifiedDateInSeconds,
                        extension = extension,
                        uri = uri
                    )
                )
            }
        }
        return filesInfo
    }


    fun loadAllFilesInfo() : List<FileInfo>{
        val filesInfo = mutableListOf<FileInfo>()

        val uri: Uri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.DATE_MODIFIED,
            MediaStore.Files.FileColumns._ID
        )
        val sortOrder = "${MediaStore.Files.FileColumns.DISPLAY_NAME} ASC"

        val cursor = contentResolver.query(uri, projection, null, null, sortOrder)

        if (cursor != null && cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
            val dateIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)

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

                filesInfo.add(
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
        return filesInfo
    }




}