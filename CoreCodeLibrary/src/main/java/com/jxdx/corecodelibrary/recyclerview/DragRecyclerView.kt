package com.jxdx.corecodelibrary.recyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
/**
 * Item能拖拽的RecyclerView。
 * @author xjh
 */
class DragRecyclerView(context: Context, attrs: AttributeSet? = null) : RecyclerView(context, attrs) {
    private var touchStartX = 0f
    private var touchStartY = 0f
    private var draggedView: View? = null

    private var originalLeft = 0
    private var originalTop = 0
    private var isDragging = false
    override fun onTouchEvent(e: MotionEvent): Boolean {
        when(e.action){
            MotionEvent.ACTION_DOWN -> {
                touchStartX = e.x
                touchStartY = e.y
                //获取被点击的子View
                draggedView = findChildViewUnder(touchStartX,touchStartY)

                if (draggedView != null) {
                    originalLeft = draggedView!!.left
                    originalTop = draggedView!!.top
                    isDragging = true
                }

            }

            MotionEvent.ACTION_MOVE -> {
                if (isDragging && draggedView != null){
                    val dx = e.x - touchStartX
                    val dy = e.y - touchStartY
                    // 更新被拖拽的子View的位置
                    draggedView!!.layout(
                        originalLeft + dx.toInt(),
                        originalTop + dy.toInt(),
                        originalLeft + dx.toInt() + draggedView!!.width,
                        originalTop + dy.toInt() + draggedView!!.height
                    )
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isDragging && draggedView != null) {
                    // 松手后吸附回原位
                    draggedView!!.layout(originalLeft, originalTop, originalLeft + draggedView!!.width, originalTop + draggedView!!.height)
                    isDragging = false
                    draggedView = null
                }
            }
        }
        //如果触摸到子View就不调用super.onTouchEvent(e)
        return isDragging || super.onTouchEvent(e)
    }
}