package com.jxdx.corecodelibrary.Compose.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jxdx.corecodelibrary.Compose.bean.DebugSwitchItem
import com.jxdx.corecodelibrary.Compose.bean.ListItem
import com.jxdx.corecodelibrary.Compose.bean.UrlItem
import com.jxdx.corecodelibrary.Compose.bean.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DebugViewModel : ViewModel() {
    // 私有的、可变的 StateFlow，作为内部状态持有者
    private val _uiState = MutableStateFlow<List<ListItem>>(emptyList())
    // 公开的、只读的 StateFlow，供 UI 订阅
    val uiState: StateFlow<List<ListItem>> = _uiState.asStateFlow()

    init {
        // ViewModel 初始化时加载初始数据
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // 在这里你可以从数据库、网络或SharedPreferences加载真实数据
            val initialItems = listOf(
                ListItem.UserInfoItem(
                    UserInfo(
                        nickname = "真实用户",
                        uid = "987654321",
                        deviceModel = "Pixel 8 Pro",
                        androidVersion = "Android 14",
                        oaid = "real-oaid-from-sdk"
                    )
                ),
                ListItem.UrlInputItem(UrlItem("https://www.google.com")),
                ListItem.DebugSwitchItemType(DebugSwitchItem(isEnabled = true))
            )
            _uiState.value = initialItems
        }
    }

    // 处理URL输入变化的事件
    fun onUrlChanged(newUrl: String) {
        val currentList = _uiState.value.toMutableList()
        val index = currentList.indexOfFirst { it is ListItem.UrlInputItem }
        if (index != -1) {
            currentList[index] = ListItem.UrlInputItem(UrlItem(newUrl))
            _uiState.value = currentList
        }
    }

    // 处理“进入”按钮点击事件
    fun onGoClicked(url: String) {
        // 在这里执行实际的逻辑，例如跳转到新的WebView Activity
        Log.d("DebugViewModel", "Go button clicked with URL: $url")
        // 示例：可以在这里启动一个新的 Activity
        // context.startActivity(...)
    }

    // 处理调试开关变化的事件
    fun onDebugSwitchChanged(isEnabled: Boolean) {
        // 在这里执行实际的逻辑，例如更新 SharedPreferences
        Log.d("DebugViewModel", "Debug switch changed to: $isEnabled")

        val currentList = _uiState.value.toMutableList()
        val index = currentList.indexOfFirst { it is ListItem.DebugSwitchItemType }
        if (index != -1) {
            currentList[index] = ListItem.DebugSwitchItemType(DebugSwitchItem(isEnabled))
            _uiState.value = currentList
        }
    }
}