package com.jxdx.corecodelibrary.demo2

import com.jxdx.corecodelibrary.common.BaseActivity
import com.jxdx.corecodelibrary.databinding.ActivityMain2Binding

class MainActivity2 : BaseActivity<ActivityMain2Binding>() {
    override fun initView() {

    }

    override fun subscribeUi() {

    }

    override fun bindLayout(): ActivityMain2Binding {
        return ActivityMain2Binding.inflate(layoutInflater)
    }

    override fun setStatusBar(): Int {
        return TRANSPARENT_STATUS_BAR_LIGHT
    }
}