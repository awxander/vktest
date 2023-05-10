package com.example.vktest

import android.graphics.drawable.Drawable
import android.widget.RadioButton

const val TAG = "VK_TEST"

fun RadioButton.setRightDrawable(drawable : Drawable?){
    setCompoundDrawablesWithIntrinsicBounds(
        null,
        null,
        drawable,
        null
    )
}

object Extensions{
        const val PNG = "png"
        const val JPG = "jpg"
        const val JPEG = "jpeg"
        const val TXT = "txt"
        const val GIF = "gif"
        const val DIRECTORY = "dir"
}