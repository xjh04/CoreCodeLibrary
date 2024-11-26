package com.jxdx.corecodelibrary.demo4

import com.jxdx.corecodelibrary.common.BaseActivity
import com.jxdx.corecodelibrary.databinding.ActivityMain4Binding

class MainActivity4 : BaseActivity<ActivityMain4Binding>() {
    override fun initView() {

    }

    override fun subscribeUi() {

    }

    override fun bindLayout(): ActivityMain4Binding {
        return ActivityMain4Binding.inflate(layoutInflater)
    }

    override fun setStatusBar(): Int {
        return TRANSPARENT_STATUS_BAR_LIGHT
    }
}