package com.mylittleproject.love42.tools

import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.mylittleproject.love42.R

@BindingAdapter("isVisible")
fun setVisible(view: View, isVisible: Boolean) {
    view.isVisible = isVisible
}

@BindingAdapter("imageURL")
fun setImageWithUrl(view: ImageView, imageURL: String?) {
    Glide.with(view)
        .load(imageURL)
        .centerCrop()
        .placeholder(R.drawable.ic_baseline_person_24)
        .into(view)
}

@BindingAdapter("imageURLNoCrop")
fun setImageWithUrlNoCrop(view: ImageView, imageURL: String?) {
    Glide.with(view)
        .load(imageURL)
        .placeholder(R.drawable.ic_baseline_person_24)
        .into(view)
}

@BindingAdapter("isMenuItemEnabled")
fun setMenuItemEnabled(view: Toolbar, isEnabled: Boolean) {
    view.menu.forEach {
        it.isEnabled = isEnabled
    }
}