package com.dicoding.githubuser.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.githubuser.data.remote.retrofit.ApiConfig
import com.dicoding.githubuser.event.Event
import com.dicoding.githubuser.data.remote.response.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowingViewModel: ViewModel() {
    private val _userFollowing = MutableLiveData<ArrayList<UserResponse>>()
    val userFollowing: LiveData<ArrayList<UserResponse>> = _userFollowing

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Event<Boolean>>()
    val isError: LiveData<Event<Boolean>> = _isError

    fun setFollowing(user: String) {
        _isLoading.value = true
        _isError.value = Event(false)
        val client = ApiConfig.getApiService().getFollowing(user)
        client.enqueue(object : Callback<ArrayList<UserResponse>> {
            override fun onResponse(
                call: Call<ArrayList<UserResponse>>,
                response: Response<ArrayList<UserResponse>>
            ) {
                _isLoading.value = false
                if (response.isSuccessful){
                    _userFollowing.value = response.body()
                } else  {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    _isError.value = Event(true)
                }
            }

            override fun onFailure(call: Call<ArrayList<UserResponse>>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                _isLoading.value = false
                _isError.value = Event(true)
            }
        })
    }

    companion object{
        private const val TAG = "FollowingViewModel"
    }
}