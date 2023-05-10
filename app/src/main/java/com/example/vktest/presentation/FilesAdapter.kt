package com.example.vktest.presentation

import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.vktest.Extensions
import com.example.vktest.R
import com.example.vktest.data.DateFormatter
import com.example.vktest.databinding.FileInfoItemBinding
import com.example.vktest.domain.entites.FileInfo

class FilesAdapter(private val onClick : (FileInfo) -> Unit) : RecyclerView.Adapter<FileInfoViewHolder>() {

    private val filesList = mutableListOf<FileInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileInfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.file_info_item, parent, false)
        return FileInfoViewHolder(view)
    }

    override fun getItemCount(): Int = filesList.size

    override fun onBindViewHolder(holder: FileInfoViewHolder, position: Int) {
        holder.bind(filesList[position], onClick)
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

    fun updateFilesList(files: List<FileInfo>) {
        deleteAll()
        insertFiles(files)
    }
}

class FileInfoViewHolder(view: View) : ViewHolder(view) {

    private val binding = FileInfoItemBinding.bind(view)


    fun bind(fileInfo: FileInfo, onClick : (FileInfo) -> Unit) {
        binding.apply {
            textViewName.text = fileInfo.name
            textViewSize.text = Formatter.formatFileSize(itemView.context, fileInfo.sizeInBytes)
            textViewCreateDate.text = DateFormatter.getDate(fileInfo.modifiedDateInSeconds)
            imageView.setImageResource(getDrawableForExtension(fileInfo.extension))
            if (fileInfo.modified)
                textViewName.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.modified_color
                    )
                )
            else
                textViewName.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        androidx.appcompat.R.color.abc_tint_default
                    )
                )

            textViewName.setOnClickListener { onClick(fileInfo) }
        }
    }

    private fun getDrawableForExtension(extension: String) =
        when (extension) {
            Extensions.PNG -> R.drawable.png_icon
            Extensions.JPEG, Extensions.JPG -> R.drawable.jpg_icon
            Extensions.TXT -> R.drawable.txt_icon
            Extensions.GIF -> R.drawable.gif_icon
            Extensions.DIRECTORY -> R.drawable.directory_icon
            else -> R.drawable.file_icon
        }

}