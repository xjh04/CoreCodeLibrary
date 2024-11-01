package com.jxdx.corecodelibrary

import androidx.recyclerview.widget.LinearLayoutManager
import com.jxdx.corecodelibrary.common.BaseActivity
import com.jxdx.corecodelibrary.databinding.ActivityMainBinding
import com.jxdx.corecodelibrary.recyclerview.DragRecyclerView

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private lateinit var recyclerView: DragRecyclerView
    override fun initView() {
        recyclerView = binding.recyclerView
        recyclerView.adapter = RecyclerViewAdapter(this, listOf(
            "xjhxjh",
            "qweqew",
            "dqw3rfqwe",
            "asdwd",
            "xjhxjh",
            "qweqew",
            "dqw3rfqwe",
            "asdwd",
            "xjhxjh",
            "qweqew",
            "dqw3rfqwe",
            "asdwd"
        ))
        recyclerView.layoutManager = LinearLayoutManager(this)
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