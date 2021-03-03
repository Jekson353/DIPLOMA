package com.samoylenko.kt12.util

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager

object AndroidUtils {
    fun hideSoftKeyBoard(ctx: Context) {
        try {
            val inputManager = ctx.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            val v = (ctx as Activity).currentFocus ?: return
            inputManager.hideSoftInputFromWindow(v.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}