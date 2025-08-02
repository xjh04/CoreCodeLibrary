package com.jxdx.corecodelibrary.Compose.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jxdx.corecodelibrary.Compose.bean.ListItem
import com.jxdx.corecodelibrary.Compose.bean.UserInfo

@Composable
fun DebugScreen(
    items: List<ListItem>,
    onUrlChanged: (String) -> Unit,
    onGoClicked: (String) -> Unit,
    onDebugSwitchChanged: (Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(items, key = { it::class.java.simpleName }) { item ->
            // 添加 key 提高性能
            when (item) {
                is ListItem.UserInfoItem -> UserInfoCard(userInfo = item.userInfo)
                is ListItem.UrlInputItem -> UrlInputCard(
                    currentUrl = item.urlItem.url,
                    onUrlChanged = onUrlChanged,
                    onGoClicked = onGoClicked
                )
                is ListItem.DebugSwitchItemType -> DebugSwitchCard(
                    isEnabled = item.debugSwitch.isEnabled,
                    onSwitchChanged = onDebugSwitchChanged
                )
            }
        }
    }
}

@Composable
fun UserInfoCard(userInfo: UserInfo, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UserInfoRow(label = "用户昵称：", value = userInfo.nickname)
            UserInfoRow(label = "uid：", value = userInfo.uid)
            UserInfoRow(label = "设备型号：", value = userInfo.deviceModel)
            UserInfoRow(label = "系统：", value = userInfo.androidVersion)
            UserInfoRow(label = "oaid：", value = userInfo.oaid)
        }
    }
}

@Composable
fun UserInfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * URL 输入卡片
 */
@Composable
fun UrlInputCard(
    currentUrl: String, // 从 initialUrl 改为 currentUrl，表示当前状态
    onUrlChanged: (String) -> Unit,
    onGoClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // 移除了 'var url by remember { mutableStateOf(initialUrl) }'
    // 现在这个组件是无状态的，完全由外部驱动

    val focusManager = LocalFocusManager.current

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = currentUrl, // 直接使用传入的 currentUrl
                onValueChange = onUrlChanged, // 每次变化时，通知 ViewModel
                placeholder = { Text("请输入url地址", fontSize = 16.sp) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(
                    onGo = {
                        onGoClicked(currentUrl)
                        focusManager.clearFocus() // 点击后收起键盘
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            Button(
                onClick = {
                    onGoClicked(currentUrl)
                    focusManager.clearFocus() // 点击后收起键盘
                },
                modifier = Modifier
                    .height(40.dp)
                    .padding(start = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("进入", fontSize = 15.sp)
            }
        }
    }
}


@Composable
fun DebugSwitchCard(
    isEnabled: Boolean,
    onSwitchChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "开启WebView 调试模式",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Switch(checked = isEnabled, onCheckedChange = onSwitchChanged)
        }
    }
}