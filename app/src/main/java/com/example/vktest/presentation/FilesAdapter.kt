package com.example.vktest.presentation

import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.vktest.R
import com.example.vktest.data.DateFormatter
import com.example.vktest.databinding.FileInfoItemBinding
import com.example.vktest.domain.entites.FileInfo

class FilesAdapter : RecyclerView.Adapter<FileInfoViewHolder>() {

    private val filesList = mutableListOf<FileInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileInfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.file_info_item, parent, false)
        return FileInfoViewHolder(view)
    }

    override fun getItemCount(): Int = filesList.size

    override fun onBindViewHolder(holder: FileInfoViewHolder, position: Int) {
        holder.bind(filesList[position])
    }

    fun insertFiles(files: List<FileInfo>) {
        filesList.addAll(files)
        notifyItemRangeInserted(filesList.size - files.size, files.size)
    }

    fun deleteAll() {
        val size = filesList.size
        filesList.clear()
        notifyItemRangeRemoved(0, size)
    }
}

class FileInfoViewHolder(view: View) : ViewHolder(view) {

    private val binding = FileInfoItemBinding.bind(view)

    companion object {
        const val PNG = "png"
        const val JPG = "jpg"
        const val JPEG = "jpeg"
        const val TXT = "txt"
        const val GIF = "gif"
        const val DIRECTORY = "dir"
    }

    fun bind(fileInfo: FileInfo) {
        binding.apply {
            textViewName.text = fileInfo.name
            textViewSize.text = Formatter.formatFileSize(itemView.context, fileInfo.sizeInBytes)
            textViewCreateDate.text = DateFormatter.getDate(fileInfo.modifiedDateInSeconds)
            imageView.setImageResource(getDrawableForExtension(fileInfo.extension))
        }
    }

    private fun getDrawableForExtension(extension: String) =
        when (extension) {
            PNG -> R.drawable.png_icon
            JPEG, JPG -> R.drawable.jpg_icon
            TXT -> R.drawable.txt
            GIF -> R.drawable.gif_icon
            DIRECTORY -> R.drawable.directory_icon
            else -> R.drawable.file_icon
        }

}