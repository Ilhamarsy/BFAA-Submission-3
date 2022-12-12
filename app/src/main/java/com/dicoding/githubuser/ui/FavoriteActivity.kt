package com.dicoding.githubuser.ui

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuser.adapter.UserAdapter
import com.dicoding.githubuser.databinding.ActivityFavoriteBinding
import com.dicoding.githubuser.model.FavoriteViewModel
import com.dicoding.githubuser.helper.ViewModelFactory
import com.dicoding.githubuser.data.remote.response.UserResponse

class FavoriteActivity : AppCompatActivity() {
    private var _binding: ActivityFavoriteBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(application)
        val viewModel by viewModels<FavoriteViewModel> {
            factory
        }

        viewModel.getUserFavorite().observe(this){
            if (it.isEmpty()){
                binding?.ivEmpty?.visibility = View.VISIBLE
            }

            val list = maplist(it)
            val adapter = UserAdapter(list)
            binding?.rvFavorite?.setHasFixedSize(true)
            binding?.rvFavorite?.adapter = adapter
            binding?.rvFavorite?.layoutManager =
                if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) GridLayoutManager(this, 2) else LinearLayoutManager(this)

            adapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
                override fun onItemClicked(data: UserResponse) {
                    sendData(data)
                }
            })
        }
    }

    private fun sendData(data: UserResponse) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("item", data)
        startActivity(intent)
    }

    private fun maplist(it: List<UserResponse>): ArrayList<UserResponse> {
        val listUser = ArrayList<UserResponse>()
        for (user in it){
            val userMapped = UserResponse(
                user.login,
                user.avatarUrl,
                user.id
            )
            listUser.add(userMapped)
        }
        return listUser
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}