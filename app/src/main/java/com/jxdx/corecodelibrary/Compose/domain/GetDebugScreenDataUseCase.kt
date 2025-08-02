package com.jxdx.corecodelibrary.Compose.domain

import com.jxdx.corecodelibrary.Compose.bean.DebugSwitchItem
import com.jxdx.corecodelibrary.Compose.bean.ListItem
import com.jxdx.corecodelibrary.Compose.bean.UrlItem
import com.jxdx.corecodelibrary.Compose.repository.UserRepository

class GetDebugScreenDataUseCase{
    private val userRepository by lazy {
        UserRepository()
    }

    suspend operator fun invoke(userId: String, oaid: String): Result<List<ListItem>> {
        // 调用仓库获取用户信息
        val userInfoResult = userRepository.getUserInfo(userId, oaid)

        return userInfoResult.map { userInfo ->
            // 如果成功，将用户信息和其他静态项组合成最终的列表
            listOf(
                ListItem.UserInfoItem(userInfo),
                ListItem.UrlInputItem(UrlItem("https://www.google.com")),
                ListItem.DebugSwitchItemType(DebugSwitchItem(isEnabled = true)) // 这个可以从 SettingsRepository 获取
            )
        }
    }
}