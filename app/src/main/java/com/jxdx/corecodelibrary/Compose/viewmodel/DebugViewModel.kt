package com.jxdx.corecodelibrary.Compose.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jxdx.corecodelibrary.Compose.bean.DebugSwitchItem
import com.jxdx.corecodelibrary.Compose.bean.ListItem
import com.jxdx.corecodelibrary.Compose.bean.UrlItem
import com.jxdx.corecodelibrary.Compose.bean.UserInfo
import com.jxdx.corecodelibrary.Compose.domain.GetDebugScreenDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DebugViewModel: ViewModel() {

    // 引入新的状态来表示加载中、成功、失败
    data class DebugScreenUiState(
        val isLoading: Boolean = true,
        var items: List<ListItem> = emptyList(),
        val error: String? = null
    )
    private val getDebugScreenDataUseCase by lazy {
        GetDebugScreenDataUseCase()
    }
    // 私有的、可变的 StateFlow，作为内部状态持有者
    private val _uiState = MutableStateFlow(DebugScreenUiState())

    // 公开的、只读的 StateFlow，供 UI 订阅
    // asStateFlow 确保 单向数据流 和 封装 特性
    val uiState: StateFlow<DebugScreenUiState> = _uiState.asStateFlow()


    init {
        // ViewModel 初始化时加载初始数据
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {

            // 1. 设置为加载中状态
            _uiState.value = DebugScreenUiState(isLoading = true)

            // 2. 调用 UseCase
            getDebugScreenDataUseCase("1", "2")
                .onSuccess { items ->
                    // 3. 如果成功，更新 UI 状态
                    _uiState.value = DebugScreenUiState(isLoading = false, items = items)
                }
                .onFailure { error ->
                    // 4. 如果失败，更新 UI 状态以显示错误信息
                    _uiState.value = DebugScreenUiState(isLoading = false, error = error.message)
                }

            // 先模拟初始话完成
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
            _uiState.value.items = initialItems
        }
    }

    fun onUrlChanged(newUrl: String) {
        val currentList = _uiState.value.items.toMutableList()
        val index = currentList.indexOfFirst { it is ListItem.UrlInputItem }
        if (index != -1) {
            currentList[index] = ListItem.UrlInputItem(UrlItem(newUrl))
            _uiState.value.items = currentList
        }
    }

    fun onGoClicked(url: String) {
        Log.d("DebugViewModel", "Go button clicked with URL: $url")
    }

    fun onDebugSwitchChanged(isEnabled: Boolean) {
        Log.d("DebugViewModel", "Debug switch changed to: $isEnabled")

        val currentList = _uiState.value.items.toMutableList()
        val index = currentList.indexOfFirst { it is ListItem.DebugSwitchItemType }
        if (index != -1) {
            currentList[index] = ListItem.DebugSwitchItemType(DebugSwitchItem(isEnabled))
            _uiState.value.items = currentList
        }
    }
}