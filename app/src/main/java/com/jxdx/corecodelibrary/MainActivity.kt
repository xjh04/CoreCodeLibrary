package com.jxdx.corecodelibrary

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jxdx.corecodelibrary.common.BaseActivity
import com.jxdx.corecodelibrary.databinding.ActivityMainBinding
import com.jxdx.corecodelibrary.recyclerview.CommonItemDecoration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var menu :RecyclerView
    override fun initView() {
        menu = binding.menu
        val data = listOf(
            "自定义Behavior",
            "自定义MPChart图表库",
            "自定义缩放",
            "自定义弹幕组件"
        )
        menu.adapter = MenuAdapter(this,data)
        menu.layoutManager = LinearLayoutManager(this)
        menu.addItemDecoration(CommonItemDecoration(0,0,5,5))
        val scope = CoroutineScope(Dispatchers.Main)

        scope.launch {

        }
    }

    override fun subscribeUi() {

    }

    override fun bindLayout(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun setStatusBar(): Int {
        return TRANSPARENT_STATUS_BAR_LIGHT
    }

    private fun main() = runBlocking {
        coroutineScope {
            launch {
                delay(1000)
                Log.d("MainActivity!", "Task 1")
            }
            delay(100)
            Log.d("MainActivity!", "Task 2")
        }
        launch {
            delay(100)
            Log.d("MainActivity!", "Task 3")
        }
        Log.d("MainActivity!", "over")
    }
}