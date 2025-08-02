package com.jxdx.corecodelibrary.common

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewbinding.ViewBinding

abstract class BaseUi<T : ViewBinding> @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr){
    init {
        init(attrs)
    }
    protected abstract fun initView()

    protected abstract fun getTypeArray(typedArray: TypedArray)

    protected abstract fun setStyleable(): IntArray

    protected abstract fun setView()

    private fun init(attrs: AttributeSet?){
        initView()
        getTypeArray(context.obtainStyledAttributes(attrs,setStyleable()))
        setView()
    }
}