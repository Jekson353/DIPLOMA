package com.samoylenko.kt12.repository

import androidx.lifecycle.LiveData
import com.samoylenko.kt12.dto.Post

interface PostRepository {
    fun getAll(): LiveData<List<Post>>
    fun likeById(id: Long)
    fun shareById(id: Long)
    fun save(post: Post)
    fun removeById(id: Long)
}