package com.dicoding.githubuser.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.githubuser.data.remote.response.UserResponse
import com.dicoding.githubuser.databinding.ItemRowUserBinding

class UserAdapter(private val listUser: ArrayList<UserResponse>): RecyclerView.Adapter<UserAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    class ListViewHolder(private var binding: ItemRowUserBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(user: UserResponse){
            Glide.with(binding.root)
                .load(user.avatarUrl)
                .circleCrop()
                .into(binding.imgItemPhoto)
            binding.tvName.text = user.login
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val binding = ItemRowUserBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listUser[position])

        holder.itemView.setOnClickListener{
            onItemClickCallback.onItemClicked(listUser[holder.adapterPosition])
        }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: UserResponse)
    }

    override fun getItemCount(): Int = listUser.size
}