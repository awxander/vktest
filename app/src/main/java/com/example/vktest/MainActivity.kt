package com.example.vktest

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.example.vktest.data.repository.LocalDataSource
import com.example.vktest.databinding.ActivityMainBinding
import com.example.vktest.domain.entites.FileInfo
import com.example.vktest.presentation.FilesAdapter
import com.example.vktest.presentation.FilesState
import com.example.vktest.presentation.FilesViewModel
import java.io.File

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel by lazy {//TODO сделать через di
        FilesViewModel(
            repository = LocalDataSource(
                contentResolver = applicationContext.contentResolver
            )
        )
    }

    private val adapter = FilesAdapter()

    private val filesInfo = mutableListOf<FileInfo>()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.permission_received),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()
        initListeners()

        viewModel.filesState.observe(this, ::handleFilesState)
    }

    private fun initListeners() {
        binding.apply {
            loadFilesButton.setOnClickListener {
                checkPermission()
                viewModel.loadFilesInfo()
            }
            setSortRadioButton(sortByNameBtn, SortType.BY_NAME)
            setSortRadioButton(sortBySizeBtn, SortType.BY_SIZE)
            setSortRadioButton(sortByExtensionBtn, SortType.BY_TYPE)
            setSortRadioButton(sortByDateBtn, SortType.BY_DATE)

            sortBtnRadioGroup.setOnCheckedChangeListener{  _, checkedId ->
                for (i in 0 until sortBtnRadioGroup.childCount) {
                    val radioButton = sortBtnRadioGroup.getChildAt(i) as RadioButton
                    if (radioButton.id != checkedId) {
                        radioButton.setRightDrawable(null)//убираем иконку при нажатии на другую кнопку
                    }
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setSortRadioButton(button : RadioButton, sortType: SortType){
        var byAsc = true//по умолчанию сортировка по возрастанию
        button.setOnClickListener {
            if (filesInfo.size > 0) {
                when(sortType){
                    SortType.BY_NAME -> filesInfo.sortBy { it.name }
                    SortType.BY_SIZE -> filesInfo.sortBy { it.sizeInBytes }
                    SortType.BY_TYPE -> filesInfo.sortBy { it.extension }
                    SortType.BY_DATE -> filesInfo.sortBy { it.modifiedDateInSeconds }
                }
                byAsc = if (byAsc) {//при нажатии на кнопку меняем порядок сортировки на противоположный
                    // и также меняем стрелку, которая этот порядок отображает
                    updateAdapterList(filesInfo)
                    button.setRightDrawable(getDrawable(R.drawable.baseline_keyboard_arrow_down_24))
                    false
                } else {
                    updateAdapterList(filesInfo.reversed())
                    button.setRightDrawable(getDrawable(R.drawable.baseline_keyboard_arrow_up_24))
                    true
                }
            }
        }
    }

    private fun updateAdapterList(filesList: List<FileInfo>) {
        adapter.deleteAll()
        adapter.insertFiles(filesList)
    }

    private fun handleFilesState(filesState: FilesState) {
        when (filesState) {
            is FilesState.Initial -> Unit
            is FilesState.Loading -> Unit//TODO добавить progressbar
            is FilesState.Error -> showErrorMsg(filesState.text)
            is FilesState.Content -> {
                filesInfo.clear()
                filesInfo.addAll(filesState.filesInfoList)
                adapter.insertFiles(filesState.filesInfoList)
            }
        }
    }

    private fun showErrorMsg(msg: String) {
        Toast.makeText(
            this@MainActivity,
            msg,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun initRecyclerView() {
        binding.fileInfoRecyclerView.adapter = adapter
    }


    private fun checkPermission() {
        if (!isPermissionGranted()) {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun isPermissionGranted(): Boolean =
        PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )


}

enum class SortType{
    BY_NAME,
    BY_SIZE,
    BY_TYPE,
    BY_DATE
}