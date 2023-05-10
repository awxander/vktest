package com.example.vktest

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.vktest.data.datasource.LocalDataSource
import com.example.vktest.data.db.FileHashDatabase
import com.example.vktest.data.repository.FilesRepositoryImpl
import com.example.vktest.databinding.ActivityMainBinding
import com.example.vktest.domain.entites.FileInfo
import com.example.vktest.presentation.FilesAdapter
import com.example.vktest.presentation.FilesState
import com.example.vktest.presentation.FilesViewModel

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel by lazy {//TODO сделать через di
        FilesViewModel(
            FilesRepositoryImpl(
                database = FileHashDatabase.getInstance(this),
                dataSource = LocalDataSource(applicationContext.contentResolver)
            )
        )
    }

    private val adapter = FilesAdapter(::onClick)

    private val filesInfo = mutableListOf<FileInfo>()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                Toast.makeText(
                    this@MainActivity, getString(R.string.permission_received), Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this@MainActivity, getString(R.string.permission_denied), Toast.LENGTH_LONG
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
        checkPermission()
        viewModel.loadFilesInfo()
    }

    private fun onClick(fileInfo: FileInfo){
        if(fileInfo.extension == Extensions.DIRECTORY){
            viewModel.loadFilesInfo(fileInfo.uri)
        }else{
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = fileInfo.uri
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            startActivity(intent)
        }
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

            loadModifiedFilesButton.setOnClickListener {
                viewModel.loadModifiedFiles()
            }

            sortBtnRadioGroup.setOnCheckedChangeListener { _, checkedId ->
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
    private fun setSortRadioButton(button: RadioButton, sortType: SortType) {
        var byAsc = true//по умолчанию сортировка по возрастанию
        button.setOnClickListener {
            if (filesInfo.size > 0) {
                when (sortType) {
                    SortType.BY_NAME -> filesInfo.sortBy { it.name }
                    SortType.BY_SIZE -> filesInfo.sortBy { it.sizeInBytes }
                    SortType.BY_TYPE -> filesInfo.sortBy { it.extension }
                    SortType.BY_DATE -> filesInfo.sortBy { it.modifiedDateInSeconds }
                }
                byAsc =
                    if (byAsc) {//при нажатии на кнопку меняем порядок сортировки на противоположный
                        // и также меняем стрелку, которая этот порядок отображает
                        adapter.updateFilesList(filesInfo)
                        button.setRightDrawable(getDrawable(R.drawable.baseline_keyboard_arrow_down_24))
                        false
                    } else {
                        adapter.updateFilesList(filesInfo.reversed())
                        button.setRightDrawable(getDrawable(R.drawable.baseline_keyboard_arrow_up_24))
                        true
                    }
            }
        }
    }

    private fun handleFilesState(filesState: FilesState) {
        when (filesState) {
            is FilesState.Initial -> Unit
            is FilesState.Loading -> Unit//TODO добавить progressbar
            is FilesState.Error -> showErrorMsg(filesState.text)
            is FilesState.Content -> {
                if (filesState.filesInfoList != null && filesState.filesInfoList.isNotEmpty()) {
                    binding.apply {
                        fileInfoRecyclerView.isVisible = true
                        infoTextView.isVisible = false
                    }
                    filesInfo.clear()
                    filesInfo.addAll(filesState.filesInfoList)
                    adapter.updateFilesList(filesState.filesInfoList)
                } else {
                    binding.apply {
                        fileInfoRecyclerView.isVisible = false
                        infoTextView.apply {
                            text = getString(R.string.no_files)
                            isVisible = true
                        }
                    }
                }
            }
        }
    }

    private fun showErrorMsg(msg: String) {
        Toast.makeText(
            this@MainActivity, msg, Toast.LENGTH_LONG
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
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        )


}

enum class SortType {
    BY_NAME, BY_SIZE, BY_TYPE, BY_DATE
}