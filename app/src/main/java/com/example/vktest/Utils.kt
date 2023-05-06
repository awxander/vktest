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