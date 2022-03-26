package com.mylittleproject.love42.tools

import android.view.View
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun View.preventDoubleClick(delayInMillis: Long = 1000L, doSomething: () -> Unit) {
    doSomething()
    isClickable = false
    findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
        delay(delayInMillis)
        isClickable = true
    }
}