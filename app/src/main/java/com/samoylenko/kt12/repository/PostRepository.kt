package com.samoylenko.kt12.repository

import androidx.lifecycle.LiveData
import com.samoylenko.kt12.dto.Post

interface PostRepository {
    fun getAll(): LiveData<List<Post>>
    fun likeById(id: Long)
    fun likesById(id: Long)
    fun dislikeById(id: Long)
    fun shareById(id: Long)
    fun save(post: Post)
    fun removeById(id: Long)
    fun onIndexPage(): LiveData<List<Post>>
    fun viewByAuthor(author: String)
}