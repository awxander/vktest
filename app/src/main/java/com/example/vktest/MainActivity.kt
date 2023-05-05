package com.example.vktest

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.vktest.data.repository.LocalDataSource
import com.example.vktest.databinding.ActivityMainBinding
import com.example.vktest.domain.entites.FileInfo
import com.example.vktest.presentation.FilesState
import com.example.vktest.presentation.FilesViewModel
import java.io.File

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel by lazy{//TODO сделать через di
        FilesViewModel(
            repository = LocalDataSource(
                contentResolver = applicationContext.contentResolver
            )
        )
    }

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
                //TODO вызов ViewModel с loadFiles
            }
        }
    }

    private fun handleFilesState(filesState: FilesState){
        when(filesState){
            is FilesState.Initial -> Unit
            is FilesState.Loading -> Unit//TODO добавить progressbar
            is FilesState.Error -> showErrorMsg(filesState.text)
            is FilesState.Content ->
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

    }


    private fun checkPermission() {
        if (!isPermissionGranted()) {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun isPermissionGranted(): Boolean =
        PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CONTACTS
        )


}