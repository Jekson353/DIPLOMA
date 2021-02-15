package com.samoylenko.kt12.viewmodel

import android.app.Application
import android.content.Context
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
    author = "Локальное сохранение",
    content = "",
    published = "",
    sharing = 0,
    like = 0,
    urlLink = "",
    image = "",
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
    fun getDemoData(context: Context) = repository.getDemoData(context)
    fun viewByAuthor(author: String) = repository.viewByAuthor(author)
    fun save() {
        edited.value?.let {
            repository.save(it)
        }
        edited.value = empty
    }
    fun noSave() {
        edited.value = empty
    }


    fun changeContent(content: String, urlLink: String, pathImage: String) {
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
        if (textUrlLink.equals("") && pathImage.equals("")) {
            edited.value = edited.value?.copy(
                published = dateText + ' ' + timeText,
                content = text,
                urlLink = "",
                image = ""
            )
        } else if (pathImage.equals("") && !textUrlLink.equals("")) {
            edited.value = edited.value?.copy(
                published = dateText + ' ' + timeText,
                content = text,
                urlLink = textUrlLink,
                image = ""
            )
        } else if (!pathImage.equals("") && textUrlLink.equals("")) {
            edited.value = edited.value?.copy(
                published = dateText + ' ' + timeText,
                content = text,
                urlLink = "",
                image = pathImage
            )
        } else {
            edited.value = edited.value?.copy(
                published = dateText + ' ' + timeText,
                content = text,
                urlLink = textUrlLink,
                image = pathImage
            )
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }
}