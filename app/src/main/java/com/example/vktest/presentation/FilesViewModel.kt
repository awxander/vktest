package com.example.vktest.presentation

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

    fun loadFilesInfo() {
        viewModelScope.launch {
            try {
                val filesInfo = repository.loadFilesInfo()
                _filesState.value = FilesState.Content(filesInfo)
            } catch (e: Exception) {
                Log.e(TAG, e.stackTrace.contentToString())
                _filesState.value = FilesState.Error(e.message.orEmpty())
            }
        }
    }


}