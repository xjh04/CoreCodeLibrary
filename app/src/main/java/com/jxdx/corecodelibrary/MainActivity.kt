package com.jxdx.corecodelibrary

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jxdx.corecodelibrary.common.BaseActivity
import com.jxdx.corecodelibrary.databinding.ActivityMainBinding
import com.jxdx.corecodelibrary.http.BaseResponseState
import com.jxdx.corecodelibrary.recyclerview.CommonItemDecoration
import kotlinx.coroutines.launch


class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var menu: RecyclerView
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun initView() {


        menu = binding.menu

        val data = listOf(
            "自定义Behavior",
            "自定义MPChart图表库",
            "自定义缩放",
            "自定义弹幕组件"
        )
        menu.adapter = MenuAdapter(this, data)
        menu.layoutManager = LinearLayoutManager(this)
        menu.addItemDecoration(CommonItemDecoration(0, 0, 5, 5))


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ifLogin.collect {
                    when (it) {
                        is BaseResponseState.Loading -> {
                            Log.d("MainActivity!", "Loading")
                        }

                        is BaseResponseState.Success -> {
                            Log.d("MainActivity!", it.data.message.toString())
                            liveData.value = "Hello World!"
                        }

                        is BaseResponseState.Error -> {
                            Log.d("MainActivity!", it.message)
                        }
                    }
                }
            }
        }


    }


    private val liveData: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    override fun subscribeUi() {

        liveData.observe(this){ s ->
            Log.d("MainActivity!", s)
        }

        liveData.value = "Hello World!"

    }

    override fun bindLayout(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun setStatusBar(): Int {
        return TRANSPARENT_STATUS_BAR_LIGHT
    }
}