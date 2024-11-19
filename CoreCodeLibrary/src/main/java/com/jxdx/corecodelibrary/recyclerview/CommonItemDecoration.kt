package com.jxdx.corecodelibrary.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class CommonItemDecoration(private val right:Int, private val left:Int, private val top:Int, private val bottom:Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.right = right
        outRect.left = left
        outRect.top = top
        outRect.bottom = bottom
    }
}