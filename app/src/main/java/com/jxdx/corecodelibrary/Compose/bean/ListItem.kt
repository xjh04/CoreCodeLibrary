package com.jxdx.corecodelibrary.Compose.bean

sealed class ListItem {
    data class UserInfoItem(val userInfo: UserInfo) : ListItem()
    data class UrlInputItem(val urlItem: UrlItem) : ListItem()
    data class DebugSwitchItemType(val debugSwitch: DebugSwitchItem) : ListItem()
}