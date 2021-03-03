package com.samoylenko.kt12.util

import java.text.DecimalFormat

object Calc {
    fun intToText(like: Int): String {
        return when (like) {
            in -10000 downTo -999999 -> calcLike(like, 0) + "K"
            in -1100 downTo -9999 -> calcLike(like, 1) + "K"
            in -1000 downTo -1099 -> "- 1K"
            in -999..0 -> like.toString()
            in 0..999 -> like.toString()
            in 1000..1099 -> "1K"
            in 1100..9999 -> calcLike(like, 1) + "K"
            in 10000..999999 -> calcLike(like, 0) + "K"
            in 1000000..999999999 -> calcLike(like, 1) + "M"
            else -> "Более 1 Billion"
        }
    }

    fun calcLike(like: Int, places: Int): String {
        //при больших цифрах, отображаемое округление уже не настолько важно
        val df = if (places == 1) DecimalFormat("###.#")
        else DecimalFormat("###")

        val liked: Double = like.toDouble() / 1000
        return df.format(liked)
    }
}