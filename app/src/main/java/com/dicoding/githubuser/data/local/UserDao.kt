package com.dicoding.githubuser.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dicoding.githubuser.data.remote.response.UserResponse

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addFavorite(user: UserResponse)

    @Query("SELECT * FROM user")
    fun loadAllFavorite(): LiveData<MutableList<UserResponse>>

    @Query("SELECT * FROM user WHERE user.id LIKE :id LIMIT 1")
    fun findById(id: Int): LiveData<UserResponse>

    @Delete
    fun deleteFavorite(user: UserResponse)
}