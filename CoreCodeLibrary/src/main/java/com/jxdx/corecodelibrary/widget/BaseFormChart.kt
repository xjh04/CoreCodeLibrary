package com.jxdx.corecodelibrary.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import kotlin.math.ceil

abstract class BaseFormChart(context: Context, attrs: AttributeSet? = null) :
    BaseChart(context, attrs),
    ScaleGestureDetector.OnScaleGestureListener {

    var step = 60f
    val margin = 100f
    var baseX = 0
    var baseY = 0
    private var previousX = 0f
    private var previousY = 0f
    private val scaleGestureDetector = ScaleGestureDetector(context, this)

    /**
     * 在画布上绘制坐标轴。
     * @param canvas 要绘制坐标轴的画布。
     */
    private fun drawAxes(canvas: Canvas) {
        val paint = Paint().apply {
            color = Color.BLACK
            strokeWidth = 3f
        }

        //上边界
        canvas.drawLine(margin, margin, width.toFloat() - margin, margin, paint)
        //下边界
        canvas.drawLine(
            margin,
            height.toFloat() - margin,
            width.toFloat() - margin,
            height.toFloat() - margin,
            paint
        )
        //左边界
        canvas.drawLine(margin, margin, margin, height.toFloat() - margin, paint)
        //右边界
        canvas.drawLine(
            width.toFloat() - margin,
            margin,
            width.toFloat() - margin,
            height.toFloat() - margin,
            paint
        )
        // 左上角箭头
        canvas.drawLine(margin, margin, margin, margin - 30f, paint)
        canvas.drawLine(
            margin + 1,
            margin - 31f,
            margin - 15f,
            margin - 15f,
            paint
        )
        canvas.drawLine(
            margin - 1,
            margin - 31f,
            margin + 15f,
            margin - 15f,
            paint
        )
        // 右下角箭头
        canvas.drawLine(
            width - margin,
            height - margin,
            width - margin + 30f,
            height - margin,
            paint
        )
        canvas.drawLine(
            width - margin + 31f,
            height - margin - 1,
            width - margin + 15f,
            height - margin - 15f,
            paint
        )
        canvas.drawLine(
            width - margin + 31f,
            height - margin + 1,
            width - margin + 15f,
            height - margin + 15f,
            paint
        )
    }

    private fun drawBottomNumbers(canvas: Canvas, baseX: Int) {
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 30f
        }

        var number = ceil(-baseX / step).toInt()
        for (i in margin.toInt() until width - margin.toInt() step step.toInt()) {
            val x = baseX % step + i
            if (x >= margin && x <= width - margin) {
                canvas.drawText(
                    number.toString(),
                    x - paint.measureText(number.toString()) / 2,
                    height - margin / 2,
                    paint
                )
                number++
            }
        }
    }

    private fun drawLeftNumbers(canvas: Canvas, baseY: Int) {
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 30f
        }

        var number = ceil(baseY / step).toInt()
        for (i in height - margin.toInt() downTo margin.toInt() step step.toInt()) {
            val y = baseY % step + i
            if (y >= margin && y <= height - margin) {
                canvas.drawText(number.toString(), margin / 2, y + paint.textSize / 2, paint)
                number++
            }
        }
    }

    private fun drawGrid(canvas: Canvas, baseX: Int, baseY: Int) {
        val paint = Paint().apply {
            color = Color.BLACK
            strokeWidth = 1f
        }
        // 垂直线
        for (i in margin.toInt() until width - margin.toInt() step step.toInt()) {
            if (baseX % step + i.toFloat() >= margin.toInt() && baseX % step + i.toFloat() <= width - margin.toInt()) {
                canvas.drawLine(
                    baseX % step + i.toFloat(),
                    margin,
                    baseX % step + i.toFloat(),
                    height.toFloat() - margin,
                    paint
                )
            }
        }
        // 水平线
        for (i in height - margin.toInt() downTo margin.toInt() step step.toInt()) {
            if (baseY % step + i.toFloat() >= margin.toInt() && baseY % step + i.toFloat() <= height - margin.toInt()) {
                canvas.drawLine(
                    margin,
                    baseY % step + i.toFloat(),
                    width.toFloat() - margin,
                    baseY % step + i.toFloat(),
                    paint
                )
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                scaleGestureDetector.onTouchEvent(event)
                baseX += (event.x - previousX).toInt()
                baseY += (event.y - previousY).toInt()
                invalidate() // 重绘
            }
        }
        previousX = event.x
        previousY = event.y
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()

        drawBottomNumbers(canvas, baseX) // 画数字
        drawLeftNumbers(canvas, baseY) // 画数字
        drawAxes(canvas)    // 画坐标轴
        drawGrid(canvas, baseX, baseY)   // 画网格
        if (data != null) {
            plotData(canvas)    // 画数据
        }

        canvas.restore()
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        step *= detector.scaleFactor
        step = step.coerceIn(50f, 100f) // 限制step的范围在50到100之间
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {

    }
}