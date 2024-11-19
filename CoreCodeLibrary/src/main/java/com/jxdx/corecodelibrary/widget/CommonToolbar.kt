package com.jxdx.corecodelibrary.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.jxdx.corecodelibrary.R
import com.jxdx.corecodelibrary.common.BaseUi
import com.jxdx.corecodelibrary.databinding.UiCommonToolbarBinding

class CommonToolbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseUi<UiCommonToolbarBinding>(context, attrs, defStyleAttr) {
    // 布局中的视图
    lateinit var toolbarBackIcon: ImageView
    lateinit var toolbarBackText: TextView
    lateinit var toolbarTitle: TextView
    lateinit var commonToolbarBackground:ConstraintLayout

    //资源
    private var titleText: String? = null
    private var backText: String? = null
    private var ifBackIcon = false
    private var ifBackText = false
    private var backgroundColor: Int? = null

    // 初始化视图
    override fun initView() {
        val binding = UiCommonToolbarBinding.inflate(LayoutInflater.from(context), this, true)
        toolbarBackIcon = binding.toolbarBackIcon
        toolbarTitle = binding.toolbarTitle
        toolbarBackText = binding.toolbarBackText
        commonToolbarBackground = binding.commonToolbar
    }


    override fun getTypeArray(typedArray: TypedArray) {
        titleText = typedArray.getString(R.styleable.CommonToolbar_titleText)
        backText = typedArray.getString(R.styleable.CommonToolbar_backText)
        ifBackIcon = typedArray.getBoolean(R.styleable.CommonToolbar_if_backIcon, true)
        ifBackText = typedArray.getBoolean(R.styleable.CommonToolbar_if_backText, true)
        backgroundColor = typedArray.getColor(R.styleable.CommonToolbar_background_color, Color.WHITE)
    }

    override fun setStyleable(): IntArray {
        return R.styleable.CommonToolbar
    }

    override fun setView() {
        toolbarTitle.text = titleText
        toolbarBackText.text = backText
        commonToolbarBackground.background = backgroundColor?.let { ColorDrawable(it) }
        //是否设置返回图标
        if(ifBackIcon){
            toolbarBackIcon.visibility = VISIBLE
        }else{
            toolbarBackIcon.visibility = GONE
        }
        //是否设置返回文字
        if(ifBackText){
            toolbarBackText.visibility = VISIBLE
        }else{
            toolbarBackText.visibility = GONE
        }
    }
}