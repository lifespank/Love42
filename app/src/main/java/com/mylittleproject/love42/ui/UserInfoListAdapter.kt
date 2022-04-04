package com.mylittleproject.love42.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.mylittleproject.love42.R
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
            item.languages.forEach {
                binding.cgLanguages.addView(
                    Chip(binding.root.context)
                        .apply {
                            text = it
                            textSize = 23f
                            setTextColor(
                                ContextCompat.getColor(
                                    binding.root.context,
                                    R.color.white
                                )
                            )
                            setChipBackgroundColorResource(R.color.purple_200)
                        })
            }
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