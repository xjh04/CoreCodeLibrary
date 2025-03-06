package com.jxdx.corecodelibrary.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.jxdx.corecodelibrary.databinding.UiBulletColorEdittextBinding

class BulletEjector @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr)  {

    private lateinit var binding: UiBulletColorEdittextBinding
    private lateinit var color1:ImageView
    private lateinit var color2:ImageView
    private lateinit var color3:ImageView
    private lateinit var editText: ColorfulEditText
    init {
        initView(context)
    }

    private fun initView(context: Context) {
        binding = UiBulletColorEdittextBinding.inflate(LayoutInflater.from(context), this, true)

        color1 = binding.color1
        color2 = binding.color2
        color3 = binding.color3
        editText = binding.edittext

        val gradientDrawable1 = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE // 明确设置为矩形
            cornerRadius = 50F
            setStroke(7, Color.BLACK)
            setColor(Color.RED)
        }

        val gradientDrawable2 = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE // 明确设置为矩形
            cornerRadius = 50F
            setStroke(7, Color.BLACK)
            setColor(Color.GREEN)
        }

        val gradientDrawable3 = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE // 明确设置为矩形
            cornerRadius = 50F
            setStroke(7, Color.BLACK)
            setColor(Color.BLUE)
        }
        color1.background = gradientDrawable1
        color2.background = gradientDrawable2
        color3.background = gradientDrawable3

//        color1.setOnClickListener{
//            editText.selectedColor = Color.RED
//        }
//
//        color2.setOnClickListener{
//            editText.selectedColor = Color.GREEN
//        }
//
//        color3.setOnClickListener{
//            editText.selectedColor = Color.BLUE
//        }

    }
}