package com.jxdx.corecodelibrary.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatEditText
import com.jxdx.corecodelibrary.data.ColorfulText

class ColorfulEditText @JvmOverloads constructor(
     context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {
     private val textPaint: Paint = Paint().apply {
          isAntiAlias = true
          textSize = textSize
     }

     var colorfulText: ColorfulText = ColorfulText(arrayListOf(), arrayListOf())

     var selectedColor : Int = Color.RED

     init {
          // 设置默认文本颜色
          setTextColor(Color.RED)
     }

     override fun onDraw(canvas: Canvas) {
          val text = colorfulText.text ?: return
          val colors = colorfulText.color ?: return

          var currentX = 0f

          // 循环处理每个文本片段并绘制不同颜色
          for (i in text.indices) {
               val subText = text[i]
               val color = colors[i]

               textPaint.color = color

               canvas.drawText(subText, currentX, baseline.toFloat(), textPaint)

               currentX += textPaint.measureText(subText)
          }
     }

     override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
          super.onTextChanged(text, start, lengthBefore, lengthAfter)
          colorfulText.text.clear()
          colorfulText.color.clear()
          colorfulText.text.add(text.toString())
          colorfulText.color.add(selectedColor)

          if (text != null) {
               colorfulText.text.add(text.toString())
               colorfulText.color.add(selectedColor)
               Log.d("ColorfulEditText", colorfulText.toString())
          }
     }
}
