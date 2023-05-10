package com.example.vktest.presentation

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vktest.TAG
import com.example.vktest.domain.repository.FilesRepository
import kotlinx.coroutines.launch

class FilesViewModel(private val repository: FilesRepository) : ViewModel() {

    private val _filesState = MutableLiveData<FilesState>()
    val filesState: LiveData<FilesState> = _filesState

    fun loadFilesInfo(uri: Uri? = null) {
        viewModelScope.launch {
            try {
                val filesInfo =
                    if (uri == null)
                        repository.loadFilesInfo()
                    else
                        repository.loadFilesInfo(uri)
                _filesState.value = FilesState.Content(filesInfo)
            } catch (e: Exception) {
                Log.e(TAG, e.stackTrace.contentToString() + e.message.orEmpty())
                _filesState.value = FilesState.Error(e.message.orEmpty())
            }
        }
    }

    fun loadModifiedFiles() {
        viewModelScope.launch {
            try {
                val filesInfo = repository.loadChangedFiles()
                _filesState.value = FilesState.Content(filesInfo)
            } catch (e: Exception) {
                Log.e(TAG, e.stackTrace.contentToString() + e.message.orEmpty())
                _filesState.value = FilesState.Error(e.message.orEmpty())
            }
        }
    }


}