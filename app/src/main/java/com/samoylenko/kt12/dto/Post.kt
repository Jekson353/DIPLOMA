package com.samoylenko.kt12.dto

data class Post (
    val id:Long,
    val author: String,
    val content: String,
    val published: String,
    val sharing: Int,
    val like: Int,
    var countVisability: Int,
    val video: String,
    val image: String,
    val likedByMe: Boolean = false
)