package com.dicoding.githubuser.data.remote.retrofit

import com.dicoding.githubuser.data.remote.response.DetailUserResponse
import com.dicoding.githubuser.data.remote.response.SearchResponse
import com.dicoding.githubuser.data.remote.response.UserResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("users")
    fun getUser(): Call<ArrayList<UserResponse>>

    @GET("search/users")
    fun getSearch(
        @Query("q") query: String
    ) : Call<SearchResponse>

    @GET("users/{username}")
    fun getDetail(
        @Path("username") username: String
    ): Call<DetailUserResponse>

    @GET("users/{username}/followers")
    fun getFollowers(
        @Path("username") username: String
    ): Call<ArrayList<UserResponse>>

    @GET("users/{username}/following")
    fun getFollowing(
        @Path("username") username: String
    ): Call<ArrayList<UserResponse>>
}