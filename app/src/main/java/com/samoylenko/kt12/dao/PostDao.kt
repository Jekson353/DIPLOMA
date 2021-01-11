package com.samoylenko.kt12.dao

import com.samoylenko.kt12.dto.Post

interface PostDao {
    fun getAll(): List<Post>
    fun likeById(id: Long)
    fun shareById(id: Long)
    fun save(post: Post): Post
    fun removeById(id: Long)
}