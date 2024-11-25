package com.jxdx.corecodelibrary.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.abs
import kotlin.math.sqrt

class ScalableImageView(context: Context, attrs: AttributeSet? = null) :
    AppCompatImageView(context, attrs) {

    private var scaleFactor: Double = 1.0 // 初始缩放比例

    private var prevDistance: Double = 0.0 // 上次两触摸点之间的距离

    //聚焦点坐标
    private var focusX = 0f
    private var focusY = 0f

    private var scaleState = true //true表示放大，false表示缩小

    private var imageViewState = 0 //0表示正常，1表示部分放大，2表示完全放大
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val action = event?.actionMasked
        val pointerCount = event?.pointerCount

        when (action) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (pointerCount == 2) {
                    prevDistance = getDistance(event)
                    focusX = (event.getX(0) + event.getX(1)) / 2
                    focusY = (event.getY(0) + event.getY(1)) / 2
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (pointerCount == 2) {
                    // 获取当前两个触摸点之间的距离
                    val currentDistance = getDistance(event)
                    // 计算两次距离的变化
                    val distanceChange = abs(currentDistance - prevDistance)
                    Log.d("ScalableImageView", "$distanceChange")
                    // 计算缩放比例
                    val scale = currentDistance / prevDistance
                    Log.d("ScalableImageView", "$scale")
                    if (scaleState && scale > 1) {
                        scaleFactor *= scale  // 放大
                    }
                    else if (scaleState && scale < 1 && distanceChange > 5) {
                        scaleState = false
                    }
                    else if (!scaleState && scale > 1 && distanceChange > 5) {
                        scaleState = true
                    }
                    else if (!scaleState && scale < 1) {
                        scaleFactor *= scale  // 缩小
                    }

                    // 计算视图的偏移量
                    val newTranslationX =
                        translationX + (focusX - width / 2) * (1 - scale)
                    val newTranslationY =
                        translationY + (focusY - height / 2) * (1 - scale)


                    // 应用缩放到 ImageView
                    scaleX = scaleFactor.toFloat()
                    scaleY = scaleFactor.toFloat()
                    translationX = newTranslationX.toFloat()
                    translationY = newTranslationY.toFloat()
                    prevDistance = currentDistance

                } else {
                    if (imageViewState == 0 || imageViewState == 1){
                        if (event.historySize > 0) {
                            val dx = event.x - event.getHistoricalX(0, 0)
                            val dy = event.y - event.getHistoricalY(0, 0)
                            translationX += dx * scaleFactor.toFloat()
                            translationY += dy * scaleFactor.toFloat()
                            Log.d("ScalableImageView", "translationX: $translationX, translationY: $translationY")
                        }
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun getDistance(event: MotionEvent): Double {
        val dx = event.getX(0) - event.getX(1)
        val dy = event.getY(0) - event.getY(1)
        return sqrt((dx * dx + dy * dy).toDouble())
    }
}
