package com.jxdx.corecodelibrary.Compose.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.jxdx.corecodelibrary.Compose.ui.DebugScreen
import com.jxdx.corecodelibrary.Compose.viewmodel.DebugViewModel

class ComposeMainActivity : AppCompatActivity() {
    // Compose 中 viewModel 的标准获取方式
    private val viewModel: DebugViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        return setContent {
            setContent {
                MaterialTheme {
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
