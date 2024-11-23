package com.jxdx.corecodelibrary.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.jxdx.corecodelibrary.data.PieChartData
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class PieChart(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private var radius: Float = 0.0f
    lateinit var data: List<PieChartData>

    fun updateData(data: List<PieChartData>) {
        this.data = data
        invalidate() // 重绘
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        radius = (min(width, height) / 4).toFloat()

        val paint = Paint().apply {
            style = Paint.Style.FILL
        }

        var startAngle = 0f
        // 遍历每个数据片段
        for (slice in data) {
            // 设置当前片段的颜色
            paint.color = getColorForSlice(slice)
            // 计算当前片段的角度
            val sweepAngle = slice.percent * 360
            // 绘制当前片段的弧形
            canvas.drawArc(
                width / 2 - radius,
                height / 2 - radius,
                width / 2 + radius,
                height / 2 + radius,
                startAngle,
                sweepAngle,
                true,
                paint
            )
            // 更新起始角度
            startAngle += sweepAngle

            // 设置文本颜色和大小
            paint.color = Color.BLACK
            paint.textSize = 30f
            // 计算文本的高度
            val textAngle = startAngle - sweepAngle / 2
            // 计算文本起始位置的坐标
            val x =
                (width / 2 + (radius * 0.7) * cos(Math.toRadians(textAngle.toDouble()))).toFloat()
            val y =
                (height / 2 + (radius * 0.7) * sin(Math.toRadians(textAngle.toDouble()))).toFloat()
            // 计算文本结束位置的坐标
            val endX =
                (width / 2 + (radius * 1.1) * cos(Math.toRadians(textAngle.toDouble()))).toFloat()
            val endY =
                (height / 2 + (radius * 1.1) * sin(Math.toRadians(textAngle.toDouble()))).toFloat()
            // 绘制从起始位置到结束位置的线
            canvas.drawLine(x, y, endX, endY, paint)
            // 计算文本的X坐标
            val textX =
                if (endX > width / 2) endX + 10 else endX - 10 - paint.measureText(slice.describe)
            val textEndX =
                if (endX > width / 2) textX + paint.measureText(slice.describe) else textX
            // 绘制从结束位置到文本位置的线
            canvas.drawLine(endX, endY, textEndX, endY, paint)
            // 绘制文本
            canvas.drawText(slice.describe, textX, endY - 5, paint)
        }
    }

    private fun getColorForSlice(slice: PieChartData): Int {
        return when (slice.describe) {
            "Category 1" -> Color.RED
            "Category 2" -> Color.GREEN
            "Category 3" -> Color.BLUE
            "Category 4" -> Color.YELLOW
            else -> Color.GRAY
        }
    }
}