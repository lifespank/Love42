package com.mylittleproject.love42.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mylittleproject.love42.data.DetailedUserInfo
import com.mylittleproject.love42.databinding.CardItemBinding

class UserInfoListAdapter :
    ListAdapter<DetailedUserInfo, RecyclerView.ViewHolder>(UserInfoCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return UserViewHolder(
            CardItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val userInfo = getItem(position)
        (holder as UserViewHolder).bind(userInfo)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        (holder as UserViewHolder).recycle()
    }

    class UserViewHolder(private val binding: CardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DetailedUserInfo) {
            binding.userInfo = item
            binding.ivProfile.clipToOutline = true
        }

        fun recycle() {
            Glide.with(binding.ivProfile).clear(binding.ivProfile)
            binding.ivProfile.setImageDrawable(null)
        }
    }
}

private class UserInfoCallback : DiffUtil.ItemCallback<DetailedUserInfo>() {

    override fun areItemsTheSame(oldItem: DetailedUserInfo, newItem: DetailedUserInfo): Boolean {
        return oldItem.intraID == newItem.intraID
    }

    override fun areContentsTheSame(oldItem: DetailedUserInfo, newItem: DetailedUserInfo): Boolean {
        return oldItem == newItem
    }
}