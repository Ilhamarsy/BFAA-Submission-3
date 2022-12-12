package com.dicoding.githubuser.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.dicoding.githubuser.R
import com.dicoding.githubuser.adapter.SectionsPagerAdapter
import com.dicoding.githubuser.databinding.ActivityDetailBinding
import com.dicoding.githubuser.event.Event
import com.dicoding.githubuser.model.DetailViewModel
import com.dicoding.githubuser.helper.ViewModelFactory
import com.dicoding.githubuser.data.remote.response.DetailUserResponse
import com.dicoding.githubuser.data.remote.response.UserResponse
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DetailActivity : AppCompatActivity() {
    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(application)
        val viewModel by viewModels<DetailViewModel> {
            factory
        }

        val intent = intent.getParcelableExtra<UserResponse>("item")
        var isFavorite = false

        if (savedInstanceState == null && intent!=null){
            viewModel.setData(intent.login)
        }

        viewModel.data.observe(this){
            setPagerAdapter(it.login)
            setData(it)
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.isError.observe(this){
            showError(it)
        }

        if (intent != null) {
            viewModel.findFavorite(intent.id).observe(this){
                if (it!=null){
                    isFavorite = true
                    activeFavoriteButton()
                }
            }
        }

        binding?.fabFav?.setOnClickListener {
            if(intent != null){
                if (isFavorite) {
                    viewModel.deleteFavorite(intent)
                    inactiveFavoriteButton()
                } else {
                    viewModel.setFavorite(intent)
                    activeFavoriteButton()
                }
                isFavorite = !isFavorite
            }
        }
    }

    private fun activeFavoriteButton(){
        binding?.fabFav?.setImageResource(R.drawable.ic_favorite_red)
    }

    private fun inactiveFavoriteButton(){
        binding?.fabFav?.setImageResource(R.drawable.ic_favorite_border_red)
    }

    private fun setPagerAdapter(login: String?) {
        if (login != null) {
            FollowFragment.user = login
        }

        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
        supportActionBar?.elevation = 0f
    }

    private fun setData(it: DetailUserResponse?) {
        if (it!=null){
            binding?.let { it1 ->
                Glide.with(this)
                    .load(it.avatarUrl)
                    .circleCrop()
                    .into(it1.imgUser)
            }
            binding?.apply {
                tvFollowers.text = it.followers.toString()
                tvFollowing.text = it.following.toString()
                tvRepository.text = it.publicRepos.toString()
                tvName.text = it.login
                tvUsername.text = it.name
                tvLocation.text = it.location
                tvCompany.text = it.company
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(it: Event<Boolean>) {
        it.getContentIfNotHandled()?.let { condition ->
            if (condition){
                binding?.let { it1 ->
                    Snackbar.make(
                        it1.root,
                        R.string.error,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object{
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.followers,
            R.string.following
        )
    }
}