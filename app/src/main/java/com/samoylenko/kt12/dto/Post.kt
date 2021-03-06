package com.samoylenko.kt12.dto

import java.io.Serializable

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val sharing: Int,
    val like: Int,
    val urlLink: String,
    val image: String,
): Serializable