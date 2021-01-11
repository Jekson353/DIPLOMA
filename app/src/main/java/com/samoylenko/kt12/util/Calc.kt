package com.samoylenko.kt12.util

import java.text.DecimalFormat

object Calc {
    fun intToText(like: Int): String {
        when (like) {
            in 0..999 -> return like.toString()
            in 1000..1099 -> return "1K"
            in 1100..9999 -> return calcLike(like, 1) + "K"
            in 10000..999999 -> return calcLike(like, 0) + "K"
            in 1000000..999999999 -> return calcLike(like, 1) + "M"
            else -> return "Более 1 Billion"
        }
    }
    fun calcLike(like: Int, places: Int): String {
        //при больших цифрах, отображаемое округление уже не настолько важно
        val df: DecimalFormat
        if (places==1){
            df = DecimalFormat("###.#")
        }else{
            df = DecimalFormat("###")
        }
        val liked: Double
        liked = like.toDouble() / 1000
        return df.format(liked)
    }
}