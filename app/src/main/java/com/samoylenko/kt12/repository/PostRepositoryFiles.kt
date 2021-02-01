package com.samoylenko.kt12.repository

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.samoylenko.kt12.dao.PostDao
import com.samoylenko.kt12.dto.Post
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class PostRepositorySQLiteImpl(
    private val dao: PostDao
) : PostRepository {
    private var posts = emptyList<Post>()
    private val data: MutableLiveData<List<Post>> = MutableLiveData(posts)


    init {
        posts = dao.getAll()
        data.value = posts
    }

    override fun onIndexPage(): LiveData<List<Post>> {
        posts = dao.getAll()
        data.value = posts
        return data
    }

    override fun getDemoData(context: Context): LiveData<List<Post>> {
        demoToSQL(context)
        posts = dao.getAll()
        data.value = posts
        return data
    }

    override fun getAll(): LiveData<List<Post>> = data

    override fun likesById(id: Long) {
        dao.likesById(id)
        posts = posts.map {
            if (it.id != id) {
                it
            } else {
                it.copy(like = it.like + 1)
            }
        }
        data.value = posts
    }

    override fun dislikeById(id: Long) {
        dao.dislikeById(id)
        posts = posts.map {
            if (it.id != id) {
                it
            } else {
                it.copy(like = it.like - 1)
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

    override fun viewByAuthor(author: String) {
        posts = posts.filter { it.author == author }
        data.value = posts
    }


//хардкодинг данных. И пересохранение файла из Drawable внутрь приложения...
    fun demoToSQL(context: Context): List<Post>{
        val uri1: Uri = Uri.parse("android.resource://com.samoylenko.kt12/drawable/demo1")
        val uri2: Uri = Uri.parse("android.resource://com.samoylenko.kt12/drawable/demo2")

        val demoPosts: List<Post> = listOf(
            Post(
                id = 101,
                author = "Пушкин А.С.",
                content = "У Лукоморья дуб зеленый, Златая цепь на дубе том...",
                published = "01.01.2018 22:45",
                sharing = 0,
                urlLink = "",
                like = 5,
                image = uri1.toString()
            ),
            Post(
                id = 102,
                author = "Лермонтов Н.Ю.",
                content = "У Лукоморья дуб зеленый, Златая цепь на дубе том...",
                published = "01.05.2018 24:45",
                sharing = 0,
                like = 2,
                urlLink = "",
                image = uri2.toString()
            ),
            Post(
                id = 103,
                author = "Неизвестный автор",
                content = "И знать не знаю, как вас звали...",
                published = "11.11.2016 03:32",
                sharing = 0,
                like = 8,
                urlLink = "www.yandex.ru",
                image = uri1.toString()
            ),
            Post(
                id = 104,
                author = "Грамилов С.В.",
                content = "У Лукоморья дуб зеленый, Златая цепь на дубе том...",
                published = "11.04.2014 09:11",
                sharing = 0,
                like = 1,
                urlLink = "",
                image = uri1.toString()
            ),
            Post(
                id = 105,
                author = "Пушкин А.С.",
                content = "У Лукоморья дуб зеленый, Златая цепь на дубе том опять текст...",
                published = "01.01.2018 22:45",
                sharing = 0,
                urlLink = "",
                like = 4,
                image = uri1.toString()
            ),
            Post(
                id = 105,
                author = "Неизвестный автор",
                content = "Темная ночь, только кошки сидят у окна, только звук от пищанья мышей, ярко глазки сверкают...",
                published = "01.07.2018 22:45",
                sharing = 0,
                urlLink = "",
                like = 4,
                image = uri1.toString()
            ),

        )
        var i = 0
        val p = demoPosts.size
        while (i<p){
            val pathImage = saveFile(demoPosts[i].image.toUri(), context)
            dao.save(demoPosts[i].copy(author = demoPosts[i].author, image = pathImage))
            i++
        }
        return demoPosts
    }

    fun saveFile(uri: Uri, context: Context): String{
        var filename2 = ""
        val name = uri.path
        if (name.equals("/drawable/demo1")){
            filename2 = "demo1.jpg"
        }else{
            filename2 = "demo2.jpg"
        }

        val inputStream: InputStream? = context.getContentResolver().openInputStream(uri)
        val pathFile = filename2.let { context.filesDir?.resolve(it) }
        val to: File = pathFile as File
        val out: OutputStream = FileOutputStream(to)

        val buf = ByteArray(1024)
        var len: Int
        if (inputStream != null) {
            while (inputStream.read(buf).also { len = it } > 0) {
                out.write(buf, 0, len)
            }
        }
        out.close()
        inputStream?.close()
       return to.absolutePath
    }
}