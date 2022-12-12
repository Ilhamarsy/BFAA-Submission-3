package com.dicoding.githubuser.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.dicoding.githubuser.data.local.UserDB
import com.dicoding.githubuser.data.local.UserDao
import com.dicoding.githubuser.data.remote.response.DetailUserResponse
import com.dicoding.githubuser.data.remote.retrofit.ApiConfig
import com.dicoding.githubuser.event.Event
import com.dicoding.githubuser.data.remote.response.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DetailViewModel(application: Application): ViewModel() {

    private val mUserDao: UserDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = UserDB.getDatabase(application)
        mUserDao = db.userDao()
    }

    private val _data = MutableLiveData<DetailUserResponse>()
    val data: LiveData<DetailUserResponse> = _data

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Event<Boolean>>()
    val isError: LiveData<Event<Boolean>> = _isError

    fun setFavorite(item: UserResponse){
        executorService.execute { mUserDao.addFavorite(item) }
    }

    fun deleteFavorite(item: UserResponse){
        executorService.execute { mUserDao.deleteFavorite(item) }
    }

    fun findFavorite(id: Int): LiveData<UserResponse> = mUserDao.findById(id)

    fun setData(user: String) {
        _isLoading.value = true
        _isError.value = Event(false)
        val client = ApiConfig.getApiService().getDetail(user)
        client.enqueue(object : Callback<DetailUserResponse> {
            override fun onResponse(
                call: Call<DetailUserResponse>,
                response: Response<DetailUserResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful){
                    _data.postValue(response.body())
                } else  {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    _isError.value = Event(true)
                }
            }

            override fun onFailure(call: Call<DetailUserResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                _isError.value = Event(true)
                _isLoading.value = false
            }
        })
    }

    companion object{
        private const val TAG = "DetailViewModel"
    }
}