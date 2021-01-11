package com.samoylenko.kt12.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.samoylenko.kt12.dao.PostDao
import com.samoylenko.kt12.dao.PostDaoImpl

class AppDb private constructor(db: SQLiteDatabase) {
    val postDao: PostDao = PostDaoImpl(db)

    companion object {
        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: AppDb(buildDatabase(context, arrayOf(PostDaoImpl.DDL)))
            }
        }

        private fun buildDatabase(context: Context, DDLs: Array<String>) = DBHelper(
            context, 1, "app.db", DDLs,
        ).writableDatabase
    }
}

class DBHelper(context: Context, dbVersion: Int, dbName: String, private val DDLs: Array<String>) :
    SQLiteOpenHelper(context, dbName, null, dbVersion) {
    override fun onCreate(db: SQLiteDatabase?) {
        DDLs.forEach {
            db?.execSQL(it)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        super.onDowngrade(db, oldVersion, newVersion)
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        super.onDowngrade(db, oldVersion, newVersion)
    }
}