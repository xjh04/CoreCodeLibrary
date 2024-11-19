package com.jxdx.corecodelibrary

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jxdx.corecodelibrary.common.BaseActivity
import com.jxdx.corecodelibrary.databinding.ActivityMainBinding
import com.jxdx.corecodelibrary.recyclerview.CommonItemDecoration

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var menu :RecyclerView
    override fun initView() {
        menu = binding.menu
        val data = listOf(
            "自定义Behavior",
            "自定义MPChart图表库"
        )
        menu.adapter = MenuAdapter(this,data)
        menu.layoutManager = LinearLayoutManager(this)
        menu.addItemDecoration(CommonItemDecoration(0,0,0,10))
    }

    override fun subscribeUi() {

    }

    override fun bindLayout(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun setStatusBar(): Int {
        return TRANSPARENT_STATUS_BAR_LIGHT
    }
}