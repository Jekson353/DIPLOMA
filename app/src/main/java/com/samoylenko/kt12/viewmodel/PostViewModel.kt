package com.samoylenko.kt12.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.samoylenko.kt12.db.AppDb
import com.samoylenko.kt12.dto.Post
import com.samoylenko.kt12.repository.PostRepository
import com.samoylenko.kt12.repository.PostRepositorySQLiteImpl
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

private val empty = Post(
    id = 0,
    author = "0",
    content = "",
    published = "",
    sharing = 0,
    like = 0,
    urlLink = "",
    image = "",
    imageUri = "",
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositorySQLiteImpl(
        AppDb.getInstance(application).postDao
    )
    val data = repository.getAll()
    val edited = MutableLiveData(empty)

    fun onIndexPage() = repository.onIndexPage()
    fun likesById(id: Long) = repository.likesById(id)
    fun dislikeById(id: Long) = repository.dislikeById(id)
    fun shareById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) = repository.removeById(id)
    fun viewByAuthor(author: String) = repository.viewByAuthor(author)
    fun save() {
        edited.value?.let {
            repository.save(it)
        }
        edited.value = empty
    }


    fun changeContent(content: String, urlLink: String, pathImage: String, uriImage: String,) {
        val text = content.trim()
        val textUrlLink = urlLink.trim()

        // Текущее время
        val currentDate = Date()
        // Форматирование времени как "день.месяц.год"
        val dateFormat: DateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val dateText: String = dateFormat.format(currentDate)
        // Форматирование времени как "часы:минуты:секунды"
        val timeFormat: DateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val timeText: String = timeFormat.format(currentDate)

        if (edited.value?.content == text && edited.value?.urlLink == textUrlLink && edited.value?.image == pathImage) {
            return
        }
        if (textUrlLink.equals("")) {
            if (pathImage.equals("")){
                edited.value = edited.value?.copy(published = dateText +' '+ timeText, content = text, urlLink = "", image = "", imageUri ="")
            }else{
                edited.value = edited.value?.copy(published = dateText +' '+ timeText, content = text, urlLink = "", image = pathImage, imageUri = uriImage)
            }
        } else if (pathImage.equals("")){
            if (textUrlLink.equals("")){
                edited.value = edited.value?.copy(published = dateText +' '+ timeText, content = text, urlLink = "", image = "", imageUri ="")
            }else{
                edited.value = edited.value?.copy(published = dateText +' '+ timeText, content = text, urlLink = "", image = "", imageUri ="")
            }
        } else {
            edited.value = edited.value?.copy(published = dateText +' '+ timeText, content = text, urlLink = textUrlLink, image = pathImage, imageUri = uriImage)
        }
    }
//    fun changeContent(content: String, video: String, image: String) {
//        val text = content.trim()
//        val textVideo = video.trim()
//
//        // Текущее время
//        val currentDate = Date()
//// Форматирование времени как "день.месяц.год"
//        val dateFormat: DateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
//        val dateText: String = dateFormat.format(currentDate)
//// Форматирование времени как "часы:минуты:секунды"
//        val timeFormat: DateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
//        val timeText: String = timeFormat.format(currentDate)
//
//        if (edited.value?.content == text && edited.value?.video == textVideo && edited.value?.image == image) {
//            return
//        }
//        if (textVideo.equals("")) {
//            if (image.equals("")){
//                edited.value = edited.value?.copy(published = dateText +' '+ timeText, content = text, video = "", image = "")
//            }else{
//                edited.value = edited.value?.copy(published = dateText +' '+ timeText, content = text, video = "", image = image)
//            }
//        } else if (image.equals("")){
//            if (textVideo.equals("")){
//                edited.value = edited.value?.copy(published = dateText +' '+ timeText, content = text, video = "", image = "")
//            }else{
//                edited.value = edited.value?.copy(published = dateText +' '+ timeText, content = text, video = "", image = "")
//            }
//        } else {
//            edited.value = edited.value?.copy(published = dateText +' '+ timeText, content = text, video = textVideo, image = image)
//        }
//    }

    fun edit(post: Post) {
        edited.value = post
    }
}