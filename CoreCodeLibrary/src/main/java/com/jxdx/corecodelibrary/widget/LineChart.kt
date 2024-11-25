package com.jxdx.corecodelibrary.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import com.jxdx.corecodelibrary.common.BaseFormChart
import com.jxdx.corecodelibrary.data.LineChartData

class LineChart(context: Context, attrs: AttributeSet? = null) : BaseFormChart(context, attrs) {
    override fun plotData(canvas: Canvas) {
        val paint = Paint().apply {
            color = Color.BLUE
            strokeWidth = 5f
            style = Paint.Style.STROKE
        }

        val path = Path()

        val firstPoint = data!![0] as LineChartData
        val startX = margin + (firstPoint.x * step) + baseX
        val startY = height - margin - (firstPoint.y * step) + baseY
        path.moveTo(startX, startY)
        for (i in 1 until data!!.size) {
            val point = data!![i] as LineChartData
            val x = margin + (point.x * step) + baseX
            val y = height - margin - (point.y * step) + baseY
            path.lineTo(x, y)
        }

        // 将画布夹到网格区域
        canvas.save()
        canvas.clipRect(margin, margin, width - margin, height - margin)
        canvas.drawPath(path, paint)
        canvas.restore()
    }
}