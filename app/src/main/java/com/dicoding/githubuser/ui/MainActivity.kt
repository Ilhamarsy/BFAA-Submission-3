package com.dicoding.githubuser.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuser.R
import com.dicoding.githubuser.adapter.UserAdapter
import com.dicoding.githubuser.databinding.ActivityMainBinding
import com.dicoding.githubuser.event.Event
import com.dicoding.githubuser.model.MainViewModel
import com.dicoding.githubuser.model.SettingViewModel
import com.dicoding.githubuser.data.remote.response.UserResponse
import com.dicoding.githubuser.helper.SettingModelFactory
import com.google.android.material.snackbar.Snackbar

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val pref = SettingPreferences.getInstance(dataStore)
        val settingViewModel = ViewModelProvider(
            this,
            SettingModelFactory(pref)
        )[SettingViewModel::class.java]

        settingViewModel.getThemeSettings().observe(this
        ) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        viewModel.user.observe(this){
            if (it.isEmpty()){
                binding?.ivEmpty?.visibility = View.VISIBLE
            } else {
                binding?.ivEmpty?.visibility = View.GONE
            }
            setData(it)
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.isError.observe(this){
            showError(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.setSearchData(query)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.setting -> {
                val setting = Intent(this, SettingActivity::class.java)
                startActivity(setting)
                true
            }
            R.id.favorite -> {
                val favorite = Intent(this, FavoriteActivity::class.java)
                startActivity(favorite)
                true
            }
            else -> true
        }
    }

    private fun setData(it: ArrayList<UserResponse>) {
        viewModel.totalCount.observe(this){
            it.getContentIfNotHandled()?.let { snackBarText ->
                binding?.let { it1 ->
                    Snackbar.make(
                        it1.root,
                        snackBarText,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

        val adapter = UserAdapter(it)
        binding?.rvUsers?.setHasFixedSize(true)
        binding?.rvUsers?.adapter = adapter
        binding?.rvUsers?.layoutManager =
            if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) GridLayoutManager(this, 2) else LinearLayoutManager(this)

        adapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: UserResponse) {
                sendData(data)
            }
        })
    }

    private fun sendData(data: UserResponse) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("item", data)
        startActivity(intent)
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
}