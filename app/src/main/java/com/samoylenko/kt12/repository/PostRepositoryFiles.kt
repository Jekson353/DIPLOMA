package com.samoylenko.kt12.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.samoylenko.kt12.dao.PostDao
import com.samoylenko.kt12.dto.Post

class PostRepositorySQLiteImpl(
    private val dao: PostDao
) : PostRepository {
    private var posts = emptyList<Post>()
    private val data: MutableLiveData<List<Post>> = MutableLiveData(posts)
    init {
        posts = dao.getAll()
        data.value = posts
    }


    override fun getAll(): LiveData<List<Post>> = data

    override fun likeById(id: Long) {
        dao.likeById(id)
        posts = posts.map {
            if (it.id != id) {
                it
            } else {
                if (it.likedByMe) {
                    it.copy(likedByMe = !it.likedByMe, like = it.like - 1)
                } else {
                    it.copy(likedByMe = !it.likedByMe, like = it.like + 1)
                }
            }
        }
        data.value = posts
    }

    override fun shareById(id: Long) {
        dao.shareById(id)
        posts = posts.map {
            if (it.id != id) {
                it
            } else {
                it.copy(sharing = it.sharing + 1)
            }
        }
        data.value = posts
    }

    override fun save(post: Post) {
        val saved = dao.save(post)
        if (post.id == 0L) {
            posts = listOf(saved) + posts
            data.value = posts
            return
        }
        posts = posts.map {
            if (it.id != post.id) it else saved
        }
        data.value = posts
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
        posts = posts.filter { it.id != id }
        data.value = posts
    }
}