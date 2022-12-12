package com.dicoding.githubuser.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuser.R
import com.dicoding.githubuser.adapter.UserAdapter
import com.dicoding.githubuser.event.Event
import com.dicoding.githubuser.model.FollowersViewModel
import com.dicoding.githubuser.model.FollowingViewModel
import com.dicoding.githubuser.data.remote.response.UserResponse
import com.dicoding.githubuser.databinding.FragmentFollowBinding
import com.google.android.material.snackbar.Snackbar

class FollowFragment : Fragment() {
    private lateinit var followersViewModel: FollowersViewModel
    private lateinit var followingViewModel: FollowingViewModel
    private var _binding: FragmentFollowBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ConstraintLayout? {
        _binding = FragmentFollowBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val index = arguments?.getInt(ARG_SECTION_NUMBER, 1)

        followersViewModel = ViewModelProvider(this)[FollowersViewModel::class.java]
        followingViewModel = ViewModelProvider(this)[FollowingViewModel::class.java]

        if (savedInstanceState == null){
            when(index){
                1 -> followersViewModel.setFollowers(user)
                2 -> followingViewModel.setFollowing(user)
            }
        }

        followersViewModel.apply {
            userFollowers.observe(viewLifecycleOwner){ showData(it) }
            isLoading.observe(viewLifecycleOwner){ showLoading(it) }
            isError.observe(viewLifecycleOwner){ showError(it) }
        }

        followingViewModel.apply {
            userFollowing.observe(viewLifecycleOwner){ showData(it) }
            isLoading.observe(viewLifecycleOwner){ showLoading(it) }
            isError.observe(viewLifecycleOwner){ showError(it) }
        }
    }

    private fun showData(it: ArrayList<UserResponse>) {
        if (it.isEmpty()){
            binding?.ivEmpty?.visibility = View.VISIBLE
        }

        val adapter = UserAdapter(it)
        binding?.rvFollowers?.setHasFixedSize(true)
        binding?.rvFollowers?.adapter = adapter
        binding?.rvFollowers?.layoutManager = LinearLayoutManager(context)

        adapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: UserResponse) {
                intent(data)
            }
        })
    }

    private fun intent(data: UserResponse) {
        val intent = Intent(this@FollowFragment.requireContext(), DetailActivity::class.java)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_SECTION_NUMBER = "section_number"
        lateinit var user: String
    }
}