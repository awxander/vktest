package com.example.vktest.presentation

import com.example.vktest.domain.entites.FileInfo

sealed interface FilesState{

    object Initial : FilesState

    object Loading : FilesState

    class Content(val filesInfoList : List<FileInfo>) : FilesState

    class Error(val text: String) : FilesState
}
