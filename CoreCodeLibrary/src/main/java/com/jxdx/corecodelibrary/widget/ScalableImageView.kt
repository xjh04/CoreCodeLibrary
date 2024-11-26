package com.jxdx.corecodelibrary.widget

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.abs
import kotlin.math.sqrt

class ScalableImageView(context: Context, attrs: AttributeSet? = null) :
    AppCompatImageView(context, attrs) {

    private val mBounceBackInterpolator: Interpolator = DecelerateInterpolator()

    private var scaleFactor: Double = 1.0 // 初始缩放比例

    private var prevDistance: Double = 0.0 // 上次两触摸点之间的距离

    //聚焦点坐标
    private var focusX = 0f
    private var focusY = 0f

    private var scaleState = true //true表示放大，false表示缩小
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val action = event?.actionMasked
        val pointerCount = event?.pointerCount

        when (action) {
            //除第一个手指之外的手指落下
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
                    // 计算缩放比例
                    val scale = currentDistance / prevDistance
                    if (scaleState && scale > 1) {
                        scaleFactor *= scale  // 放大
                    } else if (scaleState && scale < 1 && distanceChange > 2) {
                        scaleState = false
                    } else if (!scaleState && scale > 1 && distanceChange > 2) {
                        scaleState = true
                    } else if (!scaleState && scale < 1) {
                        scaleFactor *= scale  // 缩小
                    }

                    // 计算视图的偏移量
                    val newTranslationX = (translationX + (focusX - width / 2) * (1 - scale))
                    val newTranslationY =
                        translationY + (focusY - height / 2) * (1 - scale)

                    // 应用缩放到 ImageView
                    scaleX = scaleFactor.toFloat()
                    scaleY = scaleFactor.toFloat()

                    if (scaleFactor > 1.0 && getImageHeight() >= height) {
                        translationX = newTranslationX.toFloat()
                        translationY = newTranslationY.toFloat()
                    }
                    prevDistance = currentDistance
                }
                else {
                    if (event.historySize > 0 && scaleFactor > 1.0) {
                        val dx = event.x - event.getHistoricalX(0, 0)
                        val dy = event.y - event.getHistoricalY(0, 0)

                        // 限制图片的移动范围
                        if (isHorizontalCorner(dx)) {
                            translationX += dx * scaleFactor.toFloat()
                        }
                        if (isVerticalCorner(dy)) {
                            translationY += dy * scaleFactor.toFloat()
                        }
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                if (scaleFactor < 1.0) {
                    val reboundAnimatorScaleX = ObjectAnimator.ofFloat(this, "scaleX", 1.0f)
                    val reboundAnimatorScaleY = ObjectAnimator.ofFloat(this, "scaleY", 1.0f)
                    val reboundAnimatorTransX = ObjectAnimator.ofFloat(this, "translationX", 0f)
                    val reboundAnimatorTransY = ObjectAnimator.ofFloat(this, "translationY", 0f)
                    val wholeAnim = AnimatorSet()
                    wholeAnim.playTogether(
                        reboundAnimatorScaleX,
                        reboundAnimatorScaleY,
                        reboundAnimatorTransX,
                        reboundAnimatorTransY
                    )
                    wholeAnim.interpolator = mBounceBackInterpolator
                    wholeAnim.start()
                    scaleFactor = 1.0
                } else if (isRightCorner()) {
                    val reboundAnimatorTransX = ObjectAnimator.ofFloat(
                        this,
                        "translationX",
                        -(width * (scaleFactor - 1) / 2).toFloat()
                    )

                    reboundAnimatorTransX.interpolator = mBounceBackInterpolator
                    reboundAnimatorTransX.start()
                }
                else if (isLeftCorner()){
                    val reboundAnimatorTransX = ObjectAnimator.ofFloat(
                        this,
                        "translationX",
                        (width * (scaleFactor - 1) / 2).toFloat()
                    )

                    reboundAnimatorTransX.interpolator = mBounceBackInterpolator
                    reboundAnimatorTransX.start()
                }

                if (getImageHeight() - height >= 0 && isTopCorner()){
                    val reboundAnimatorTransY = ObjectAnimator.ofFloat(this, "translationY", ((getImageHeight() - height)/ 2))
                    reboundAnimatorTransY.interpolator = mBounceBackInterpolator
                    reboundAnimatorTransY.start()
                }
                else if (getImageHeight() - height >= 0 && isBottomCorner()){
                    val reboundAnimatorTransY = ObjectAnimator.ofFloat(this, "translationY", -((getImageHeight() - height) / 2))
                    reboundAnimatorTransY.interpolator = mBounceBackInterpolator
                    reboundAnimatorTransY.start()
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

    private fun getImageHeight(): Float {
        val drawable = drawable ?: return 0.0f
        val imageHeight = (drawable.intrinsicHeight * width).toFloat() / drawable.intrinsicWidth
        return ((imageHeight * scaleFactor).toFloat())
    }

    private fun isHorizontalCorner(dx: Float): Boolean {
        return (abs(translationX + dx * scaleFactor) < width * (scaleFactor - 1) / 2)
    }

    private fun isVerticalCorner(dy: Float): Boolean {
        return getImageHeight() >= height && abs(translationY + dy * scaleFactor) < (getImageHeight() - height) / 2
    }

    private fun isRightCorner(): Boolean {
        val maxTranslationX = width * (scaleFactor - 1) / 2
        return -translationX > maxTranslationX
    }

    private fun isLeftCorner(): Boolean {
        val maxTranslationX = width * (scaleFactor - 1) / 2
        return translationX > maxTranslationX
    }

    private fun isTopCorner(): Boolean {
        val maxTranslationY = (getImageHeight() - height) / 2
        return  translationY > maxTranslationY
    }

    private fun isBottomCorner(): Boolean {
        val maxTranslationY = (getImageHeight() - height) / 2
        return  -translationY > maxTranslationY
    }
}