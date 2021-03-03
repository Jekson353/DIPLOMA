package com.samoylenko.kt12.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.samoylenko.kt12.dto.Post

@Entity
data class PostEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val sharing: Int,
    val likes: Int,
    val urlLink: String,
    val image: String,
){
    companion object{
        fun  fromPost(post: Post): PostEntity =  with(post){
            PostEntity(id, author, content, published, sharing, like, urlLink, image)
        }
    }
}

fun PostEntity.toPost(): Post = Post (id, author, content, published, sharing, likes, urlLink, image)