package com.jxdx.corecodelibrary.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.jxdx.corecodelibrary.data.ColorfulText

class ColorfulTextView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    var text: ColorfulText = ColorfulText(arrayListOf(), arrayListOf())
    var textDrawSize = 0f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val paint = Paint().apply {
            textSize = textDrawSize
            color = color
        }

        val textWidth = paint.measureText(getLongestText())
        val textHeight = (paint.fontMetrics.bottom - paint.fontMetrics.top) * getMaxLines()

        val desiredWidth = (textWidth + paddingLeft + paddingRight).toInt()
        val desiredHeight = (textHeight + paddingTop + paddingBottom).toInt()

        setMeasuredDimension(
            resolveSize(desiredWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val paint = Paint().apply {
            textSize = textDrawSize
            color = color
        }

        var xOffset = paddingLeft.toFloat()
        var yOffset = paddingTop - paint.fontMetrics.top

        for (i in text.text.indices) {
            if (text.text[i] == "\n"){
                yOffset += paint.fontMetrics.bottom - paint.fontMetrics.top
                xOffset = paddingLeft.toFloat()
            }
            else{
                paint.color = text.color[i]
                canvas.drawText(text.text[i], xOffset, yOffset, paint)
                xOffset += paint.measureText(text.text[i])
            }

        }
    }

    private fun getLongestText(): String {

        val paint = Paint().apply {
            textSize = textDrawSize
            color = color
        }

        var maxString = ""
        for (i in text.text.indices){
            if (paint.measureText(text.text[i]) > paint.measureText(maxString)){
                maxString = text.text[i]
            }
        }
        return maxString
    }

    private fun getMaxLines(): Int {
        return text.text.count { it == "\n" } + 1
    }
}