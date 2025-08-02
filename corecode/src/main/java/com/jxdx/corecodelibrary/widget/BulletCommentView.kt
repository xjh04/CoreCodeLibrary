package com.jxdx.corecodelibrary.widget


import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.jxdx.corecodelibrary.data.ColorfulText
import com.jxdx.corecodelibrary.databinding.UiBulletCommentBinding


class BulletCommentView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    //透明度
    var transparency = 1f
    private lateinit var binding: UiBulletCommentBinding

    //图片和彩色文字
    private lateinit var bulletCommentText: ColorfulTextView
    private lateinit var bulletCommentPic: ImageView

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        alpha = 0f
        binding = UiBulletCommentBinding.inflate(LayoutInflater.from(context), this, true)
        bulletCommentText = binding.bulletCommentText
        bulletCommentPic = binding.bulletCommentPic
    }

    fun setText(text : ColorfulText){
        bulletCommentText.text = text

        bulletCommentText.requestLayout() //重新测量
        bulletCommentText.invalidate() //重新绘制
    }

    fun setTextSize(size : Float){
        bulletCommentText.textDrawSize = size

        bulletCommentText.requestLayout() //重新测量
        bulletCommentText.invalidate() //重新绘制
    }

    fun advance(start : Float, end : Float) {
        bulletCommentText.post{
            alpha = transparency
            ObjectAnimator.ofFloat(this, "translationX", start, end-width.toFloat()).apply {
                duration = 7000
                interpolator = LinearInterpolator() // 线性插值器，匀速移动
                start()
            }
        }
    }
}