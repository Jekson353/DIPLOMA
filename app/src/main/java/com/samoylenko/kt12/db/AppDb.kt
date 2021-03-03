package com.samoylenko.kt12.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Database
//import android.database.sqlite.SQLiteOpenHelper
import androidx.room.Room
import androidx.room.RoomDatabase
import com.samoylenko.kt12.dao.PostDao
import com.samoylenko.kt12.entity.PostEntity

//import com.samoylenko.kt12.dao.PostDaoImpl

@Database(entities = [PostEntity::class], version = 1)
abstract class AppDb: RoomDatabase() {
    abstract val postDao: PostDao

    companion object {
        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also {
                    instance = it
                }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDb::class.java, "app1.db")
                .allowMainThreadQueries()
                .build()
//        private fun buildDatabase(context: Context, DDLs: Array<String>) = DBHelper(
//            context, 1, "app.db", DDLs,
//        ).writableDatabase
    }
}

//class DBHelper(context: Context, dbVersion: Int, dbName: String, private val DDLs: Array<String>) :
//    SQLiteOpenHelper(context, dbName, null, dbVersion) {
//    override fun onCreate(db: SQLiteDatabase?) {
//        DDLs.forEach {
//            db?.execSQL(it)
//        }
//    }
//
//    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
//        super.onDowngrade(db, oldVersion, newVersion)
//    }
//
//    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
//        super.onDowngrade(db, oldVersion, newVersion)
//    }
//}