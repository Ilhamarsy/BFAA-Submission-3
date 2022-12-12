package com.dicoding.githubuser.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.githubuser.data.remote.response.UserResponse
import com.dicoding.githubuser.data.remote.retrofit.ApiConfig
import com.dicoding.githubuser.event.Event
import com.dicoding.githubuser.data.remote.response.SearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel: ViewModel() {
    private val _user = MutableLiveData<ArrayList<UserResponse>>()
    val user: LiveData<ArrayList<UserResponse>> = _user

    private val _totalCount = MutableLiveData<Event<String>>()
    val totalCount: LiveData<Event<String>> = _totalCount

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Event<Boolean>>()
    val isError: LiveData<Event<Boolean>> = _isError

    init {
        setData()
    }

    private fun setData() {
        _isLoading.value = true
        _isError.value = Event(false)
        val client = ApiConfig.getApiService().getUser()
        client.enqueue(object : Callback<ArrayList<UserResponse>> {
            override fun onResponse(
                call: Call<ArrayList<UserResponse>>,
                response: Response<ArrayList<UserResponse>>
            ) {
                _isLoading.value = false
                if (response.isSuccessful){
                    _user.value = response.body()
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

    fun setSearchData(user: String) {
        _isLoading.value = true
        _isError.value = Event(false)
        val client = ApiConfig.getApiService().getSearch(user)
        client.enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful){
                    _totalCount.value = Event("Found ${response.body()?.totalCount.toString()} users")
                    _user.value = response.body()?.items
                } else  {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    _isError.value = Event(true)
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                _isLoading.value = false
                _isError.value = Event(true)
            }
        })
    }

    companion object{
        private const val TAG = "MainViewModel"
    }
}