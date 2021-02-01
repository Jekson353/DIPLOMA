package com.samoylenko.kt12.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.samoylenko.kt12.dto.Post

class PostDaoImpl(private val db: SQLiteDatabase) : PostDao {
    companion object {
        val DDL = """
            CREATE TABLE ${PostColumns.TABLE}(
                ${PostColumns.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${PostColumns.COLUMN_AUTHOR} TEXT NOT NULL,
                ${PostColumns.COLUMN_CONTENT} TEXT NOT NULL,
                ${PostColumns.COLUMN_PUBLISHED} TEXT NOT NULL,
                ${PostColumns.COLUMN_LIKES} INTEGER NOT NULL DEFAULT 0,
                ${PostColumns.COLUMN_SHARED} INTEGER NOT NULL DEFAULT 0,
                ${PostColumns.COLUMN_IMAGE} TEXT DEFAULT "",
                ${PostColumns.COLUMN_URL_LINK} TEXT DEFAULT ""
            );
            """.trimIndent()
    }

    object PostColumns {
        const val TABLE = "posts"
        const val COLUMN_ID = "id"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_PUBLISHED = "published"
        const val COLUMN_LIKES = "likes"
        const val COLUMN_SHARED = "shared"
        const val COLUMN_IMAGE = "image"
        const val COLUMN_URL_LINK = "urlLink"
        val ALL_COLUMNS = arrayOf(
            COLUMN_ID,
            COLUMN_AUTHOR,
            COLUMN_CONTENT,
            COLUMN_PUBLISHED,
            COLUMN_LIKES,
            COLUMN_SHARED,
            COLUMN_IMAGE,
            COLUMN_URL_LINK
        )
    }

    override fun getAll(): List<Post> {
        val posts = mutableListOf<Post>()
        db.query(
            PostColumns.TABLE,
            PostColumns.ALL_COLUMNS,
            null,
            null,
            null,
            null,
            "${PostColumns.COLUMN_LIKES} DESC"
        ).use {
            while (it.moveToNext()) {
                posts.add(map(it))
            }
        }
        return posts
    }

    override fun likesById(id: Long) {
        db.execSQL(
            """
                UPDATE posts SET
                    likes = likes + 1
                WHERE id = ?;
            """.trimIndent(), arrayOf(id)
        )
    }

    override fun dislikeById(id: Long) {
        db.execSQL(
            """
                UPDATE posts SET
                    likes = likes - 1
                WHERE id = ?;
            """.trimIndent(), arrayOf(id)
        )
    }

    override fun shareById(id: Long) {
        db.execSQL(
            """
                UPDATE posts SET
                    shared = shared + 1
                WHERE id = ?;
            """.trimIndent(), arrayOf(id)
        )
    }

    override fun save(post: Post): Post {
        val values = ContentValues().apply {
            if (post.id != 0L) {
                put(PostColumns.COLUMN_ID, post.id)
            }
            if (!post.author.equals("")) {
                put(PostColumns.COLUMN_AUTHOR, post.author)
            } else {
                put(PostColumns.COLUMN_AUTHOR, "Локальное сохранение")
            }
            put(PostColumns.COLUMN_CONTENT, post.content)
            put(PostColumns.COLUMN_PUBLISHED, post.published)
            put(PostColumns.COLUMN_LIKES, post.like)
            put(PostColumns.COLUMN_SHARED, post.sharing)
            put(PostColumns.COLUMN_IMAGE, post.image)
            put(PostColumns.COLUMN_URL_LINK, post.urlLink)
        }
        val id = db.replace(PostColumns.TABLE, null, values)
        db.query(
            PostColumns.TABLE,
            PostColumns.ALL_COLUMNS,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null,
        ).use {
            it.moveToNext()
            return map(it)
        }
    }

    override fun removeById(id: Long) {
        db.delete(
            PostColumns.TABLE,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }

    private fun map(cursor: Cursor): Post {
        with(cursor) {
            return Post(
                id = getLong(getColumnIndexOrThrow(PostColumns.COLUMN_ID)),
                author = getString(getColumnIndexOrThrow(PostColumns.COLUMN_AUTHOR)),
                content = getString(getColumnIndexOrThrow(PostColumns.COLUMN_CONTENT)),
                published = getString(getColumnIndexOrThrow(PostColumns.COLUMN_PUBLISHED)),
                like = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_LIKES)),
                sharing = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_SHARED)),
                image = getString(getColumnIndexOrThrow(PostColumns.COLUMN_IMAGE)),
                urlLink = getString(getColumnIndexOrThrow(PostColumns.COLUMN_URL_LINK))
            )
        }
    }
}