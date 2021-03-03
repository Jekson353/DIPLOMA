package com.samoylenko.kt12.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.samoylenko.kt12.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<PostEntity>>

    @Query("UPDATE PostEntity SET likes = likes + 1 WHERE id = :id")
    fun likesById(id: Long)

    @Query("UPDATE PostEntity SET likes = likes - 1 WHERE id = :id")
    fun dislikeById(id: Long)

    @Query("UPDATE PostEntity SET sharing = sharing + 1 WHERE id = :id")
    fun shareById(id: Long)

    @Insert
    fun insertPost(post: PostEntity)
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updatePost(post: PostEntity)

    @Query("SELECT id FROM PostEntity WHERE id = :id LIMIT 1")
    fun getPostId(id: Long): Long?

    fun save(post: PostEntity){
        val id = getPostId(post.id)
        if (id == null){
            insertPost(post)
        }else{
            updatePost(post)
        }
    }

    @Query("DELETE FROM PostEntity WHERE id = :id")
    fun removeById(id: Long)
}