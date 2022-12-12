package com.dicoding.githubuser.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.githubuser.data.remote.response.UserResponse

@Database(entities = [UserResponse::class], version = 1)
abstract class UserDB : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: UserDB? = null
        @JvmStatic
        fun getDatabase(context: Context): UserDB {
            if (INSTANCE == null) {
                synchronized(UserDB::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        UserDB::class.java, "note_database")
                        .build()
                }
            }
            return INSTANCE as UserDB
        }
    }
}