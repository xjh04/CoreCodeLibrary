package com.jxdx.corecodelibrary.demo3

import android.util.Log
import androidx.viewpager2.widget.ViewPager2
import com.jxdx.corecodelibrary.common.BaseActivity
import com.jxdx.corecodelibrary.databinding.ActivityMain3Binding
import com.jxdx.corecodelibrary.widget.ScalableImageView

class MainActivity3 : BaseActivity<ActivityMain3Binding>() {
    private lateinit var viewPager2: ViewPager2
    private lateinit var imageView: ScalableImageView
    override fun initView() {
//        viewPager2 = binding.viewPager2
//        viewPager2.isUserInputEnabled = false
//        viewPager2.adapter = ViewPage2Adapter(this, listOf(
//            1,
//            2,
//            3
//        ))
        imageView = binding.imageView
        imageView.setOnClickListener{
            Log.d("MainActivity3", "onClick")
        }

    }

    override fun subscribeUi() {

    }

    override fun bindLayout(): ActivityMain3Binding {
        return ActivityMain3Binding.inflate(layoutInflater)
    }

    override fun setStatusBar(): Int {
        return TRANSPARENT_STATUS_BAR_LIGHT
    }
}