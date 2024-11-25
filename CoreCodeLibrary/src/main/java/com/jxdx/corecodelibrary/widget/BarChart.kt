package com.jxdx.corecodelibrary.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import com.jxdx.corecodelibrary.data.BarChartData

class BarChart(context: Context, attrs: AttributeSet? = null) : BaseFormChart(context, attrs) {
    override fun plotData(canvas: Canvas) {
        val paint = Paint().apply {
            color = Color.BLUE
            style = Paint.Style.FILL
        }

        // 将画布夹到网格区域
        canvas.save()
        canvas.clipRect(margin, margin, width - margin, height - margin)

        for (point in data!!) {
            if (point is BarChartData) {
                val left = margin + (point.x * step) + baseX
                val top = height - margin - (point.height * step) + baseY
                val right = margin + (point.x * step) + baseX + step
                val bottom = height - margin + baseY
                canvas.drawRect(left, top, right, bottom, paint)
            }
        }

        canvas.restore()
    }
}