package com.jxdx.corecodelibrary.common

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.viewbinding.ViewBinding


abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {

    companion object {
        const val FULL_SCREEN = 1
        const val TRANSPARENT_STATUS_BAR_DARK = 2
        const val TRANSPARENT_STATUS_BAR_LIGHT = 3
    }
    lateinit var binding: T

    private var controller: WindowInsetsController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = bindLayout()
        setContentView(binding.root)
        // 隐藏actionbar
        supportActionBar?.hide()
        //设置状态栏
        when (setStatusBar()) {
            FULL_SCREEN -> {
                setFullScreen()
            }

            TRANSPARENT_STATUS_BAR_DARK -> {
                setTranslucentStatus(true)
            }

            TRANSPARENT_STATUS_BAR_LIGHT -> {
                setTranslucentStatus(false)
            }
        }

        initView()
        subscribeUi()
    }


    /**设置全屏模式  */
    private fun setFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.apply {
                // 设置全屏沉浸式布局
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                hide(WindowInsets.Type.systemBars())
            }
        }
    }

    /**设置状态栏为透明，并且留出安全距离  */
    private fun setTranslucentStatus(isDarkTheme: Boolean) {
        val window = this.window
        val decorView = window.decorView
        // 两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
        val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        decorView.systemUiVisibility = option
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT

        binding.root.setOnApplyWindowInsetsListener { v, insets ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                val systemBars =
                    insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
                v.updatePadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            }
            insets
        }

        if (isDarkTheme) {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        } else {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

    }

    /** 初始化控件  */
    protected abstract fun initView()

    /** 订阅ViewModel  */
    protected abstract fun subscribeUi()

    /** 绑定binding  */
    protected abstract fun bindLayout(): T

    protected abstract fun setStatusBar(): Int
}