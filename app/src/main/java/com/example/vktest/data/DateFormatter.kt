package com.example.vktest.data

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {
    @SuppressLint("SimpleDateFormat")
    fun getDate(timeInSeconds : Long) : String{
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val date = Date(timeInSeconds * 1000L)
        return formatter.format(date)
    }
}