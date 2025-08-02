package com.jxdx.corecodelibrary.Compose.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import com.jxdx.corecodelibrary.Compose.ui.DebugScreen
import com.jxdx.corecodelibrary.Compose.ui.LightBlueTheme
import com.jxdx.corecodelibrary.Compose.viewmodel.DebugViewModel
import com.jxdx.corecodelibrary.R

class ComposeMainActivity : AppCompatActivity() {
    // Compose 中 viewModel 的标准获取方式
    private val viewModel: DebugViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 设置状态栏颜色
        window.statusBarColor = getColor(R.color.white) // 或者直接使用颜色值
        // 如果状态栏是浅色，设置状态栏图标为深色
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true

        return setContent {
            setContent {
                LightBlueTheme {
                    // 有状态的控件
                    // 从 ViewModel 中收集状态
                    val items by viewModel.uiState.collectAsState()

                    DebugScreen(
                        items = items.items,
                        // 将 UI 事件委托给 ViewModel
                        onUrlChanged = viewModel::onUrlChanged,
                        onGoClicked = viewModel::onGoClicked,
                        onDebugSwitchChanged = viewModel::onDebugSwitchChanged
                    )
                }
            }
        }
    }
}
