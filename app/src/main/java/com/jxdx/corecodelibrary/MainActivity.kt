package com.jxdx.corecodelibrary

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jxdx.corecodelibrary.common.BaseActivity
import com.jxdx.corecodelibrary.databinding.ActivityMainBinding
import com.jxdx.corecodelibrary.recyclerview.MyRecyclerView
import okhttp3.OkHttpClient

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private lateinit var recyclerView: MyRecyclerView
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