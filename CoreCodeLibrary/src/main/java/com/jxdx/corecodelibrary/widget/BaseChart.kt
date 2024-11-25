package com.jxdx.corecodelibrary.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

abstract class BaseChart(context: Context, attrs: AttributeSet? = null) : View(context, attrs){
    var data: List<*>? = null
    fun updateData(data: List<*>){
        this.data = data
        invalidate() // 重绘
    }
    abstract fun plotData(canvas: Canvas)
}