package com.jxdx.corecodelibrary.demo1

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.RecyclerView
import com.jxdx.corecodelibrary.R

class MyRecyclerView(context: Context, attrs: AttributeSet? = null) : RecyclerView(context, attrs) {
    private var lastY = 0f
    private var isOpen = false
    private lateinit var textView: TextView

    override fun onTouchEvent(e: MotionEvent?): Boolean {
        overScrollMode = OVER_SCROLL_NEVER

        val parent = parent as MotionLayout
        textView = parent.getViewById(R.id.text) as TextView

        if (e != null) {
            when (e.action) {
                MotionEvent.ACTION_DOWN -> {
                    translationY = if(isOpen){
                        500f
                    }else{
                        0f
                    }

                    lastY = e.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val dy = e.y - lastY
                    if(dy > 0){
                        if (!canScrollVertically(-1)) {
                            translationY += dy / 2
                            textView.alpha -= 0.1f
                            if(translationY >= 200){
                                Log.d("qweqew","open")
                                isOpen = true
                            }
                        }
                        lastY = e.y
                    }else{
                        return if(isOpen){
                            translationY += dy
                            if(textView.alpha<1){
                                textView.alpha += 0.1f
                            }else{
                                textView.alpha =1f
                            }

                            if(translationY <= 0f){
                                isOpen = false
                            }
                            true
                        } else{
                            super.onTouchEvent(e)
                        }
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if(!isOpen){
                        translationY = 0f
                        isOpen = false
                        textView.alpha = 1f
                    }else{
                        translationY = 500F
                        textView.alpha = 0f
                    }
                }
            }
        }
        return super.onTouchEvent(e)
    }
}