package com.mylittleproject.love42.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int) :
    ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // item position
        val index = (view.layoutParams as StaggeredGridLayoutManager.LayoutParams).spanIndex
        if (position < spanCount) {
            outRect.left = spacing
            outRect.right = spacing
        }
        outRect.right = spacing
        when (index) {
            0 -> {
                outRect.top = spacing
                outRect.bottom = spacing / 2
            }
            spanCount - 1 -> {
                outRect.top = spacing / 2
                outRect.bottom = spacing
            }
            else -> {
                outRect.top = spacing / 2
                outRect.bottom = spacing / 2
            }
        }
    }
}