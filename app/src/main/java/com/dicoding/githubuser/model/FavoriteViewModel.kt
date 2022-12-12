package com.dicoding.githubuser.model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.githubuser.data.local.UserDB
import com.dicoding.githubuser.data.local.UserDao
import com.dicoding.githubuser.data.remote.response.UserResponse

class FavoriteViewModel(application: Application): ViewModel() {
    private val mUserDao: UserDao
    init {
        val db = UserDB.getDatabase(application)
        mUserDao = db.userDao()
    }

    fun getUserFavorite() : LiveData<MutableList<UserResponse>> = mUserDao.loadAllFavorite()
}