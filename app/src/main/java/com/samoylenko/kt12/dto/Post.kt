package com.samoylenko.kt12.dto

data class Post (
    val id:Long,
    val author: String,
    val content: String,
    val published: String,
    val sharing: Int,
    val like: Int,
    val urlLink: String,
    val image: String,
    //val imageUri: String
)