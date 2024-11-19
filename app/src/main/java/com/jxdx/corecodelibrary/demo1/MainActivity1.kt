package com.jxdx.corecodelibrary.demo1

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jxdx.corecodelibrary.common.BaseActivity
import com.jxdx.corecodelibrary.databinding.ActivityMain1Binding

class MainActivity1 : BaseActivity<ActivityMain1Binding>() {

    private lateinit var recyclerView: RecyclerView
    override fun initView() {
        recyclerView = binding.recyclerView
        recyclerView.adapter = RecyclerViewAdapter(
            listOf(
                RecyclerData(1, "xjhxjh"),
                RecyclerData(1, "qwd"),
                RecyclerData(2, "27"),
                RecyclerData(1, "xjhxjh"),
                RecyclerData(1, "qwd"),
                RecyclerData(2, "27"),
                RecyclerData(1, "xjhxjh"),
                RecyclerData(1, "qwd"),
                RecyclerData(2, "27"),
                RecyclerData(1, "xjhxjh"),
                RecyclerData(1, "qwd"),
                RecyclerData(2, "27"),
                RecyclerData(1, "xjhxjh"),
                RecyclerData(1, "qwd"),
                RecyclerData(2, "27"),
                RecyclerData(1, "xjhxjh"),
                RecyclerData(1, "qwd"),
                RecyclerData(2, "27"),
                RecyclerData(1, "xjhxjh"),
                RecyclerData(1, "qwd"),
                RecyclerData(2, "27"),
                RecyclerData(1, "xjhxjh"),
                RecyclerData(1, "qwd"),
                RecyclerData(2, "27"),
            )
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun subscribeUi() {
    }

    override fun bindLayout(): ActivityMain1Binding {
        return ActivityMain1Binding.inflate(layoutInflater)
    }

    override fun setStatusBar(): Int {
        return TRANSPARENT_STATUS_BAR_LIGHT
    }
}