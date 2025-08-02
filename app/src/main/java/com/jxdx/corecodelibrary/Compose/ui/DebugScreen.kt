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

/**
 * DebugScreen 是一个 "无状态" (Stateless) 的 Composable 函数。
 * "无状态" 意味着它自身不持有任何可变状态（如 remember { mutableStateOf(...) }）。
 * 它的所有行为和显示内容都由传入的参数决定。这使得它非常易于预览、测试和复用。
 *
 * @param items 这是一个列表，包含了所有需要在屏幕上展示的数据项。
 *              它是一个 sealed class (ListItem) 的列表，这使得我们可以用 when 语句来处理不同类型的数据项。
 * @param onUrlChanged 一个函数回调，当 URL 输入框的内容改变时会被调用。
 *                     它接收一个新的 String 作为参数。
 * @param onGoClicked 一个函数回调，当用户点击 "进入" 按钮时被调用。
 *                    它接收当前的 URL 字符串作为参数。
 * @param onDebugSwitchChanged 一个函数回调，当用户切换调试开关时被调用。
 *                             它接收一个新的 Boolean 值 (true 或 false) 作为参数。
 */
@Composable
fun DebugScreen(
    items: List<ListItem>,
    onUrlChanged: (String) -> Unit,
    onGoClicked: (String) -> Unit,
    onDebugSwitchChanged: (Boolean) -> Unit
) {
    LazyColumn(
        // Modifier 用于修饰或给 Composable 添加行为，如大小、内边距、点击事件等。
        modifier = Modifier
            .fillMaxSize()
            // 左右两边添加 16dp 的内边距。
            .padding(horizontal = 16.dp),

        // 会在每个列表项之间自动添加 12dp 的垂直间距。
        verticalArrangement = Arrangement.spacedBy(12.dp),
        // 会在列表的顶部和底部分别添加 16dp 的内边距，
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // 我们使用 'items' 这个扩展函数来根据一个列表动态生成所有的列表项。
        items(
            items = items,

            // 使用每个列表项的类名作为 key，
            // 当列表数据发生变化时，Compose 可以通过 key 来识别哪些项是真正改变了，
            // 从而只重组改变了的项，而不是整个列表。
            key = { item -> item::class.java.simpleName }
        ) { item -> // lambda 会为列表中的每一项被调用一次。

            // when语句是处理 sealed class 的完美方式。
            when (item) {
                is ListItem.UserInfoItem -> {
                    UserInfoCard(userInfo = item.userInfo)
                }

                is ListItem.UrlInputItem -> {
                    // onUrlChanged 和 onGoClicked这两个事件回调直接从 DebugScreen 的参数透传下去。
                    // 这种模式称为 "状态提升"，子组件的事件通知父组件处理。
                    UrlInputCard(
                        currentUrl = item.urlItem.url,
                        onUrlChanged = onUrlChanged,
                        onGoClicked = onGoClicked
                    )
                }

                is ListItem.DebugSwitchItemType -> {
                    DebugSwitchCard(
                        isEnabled = item.debugSwitch.isEnabled,
                        onSwitchChanged = onDebugSwitchChanged
                    )
                }
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
    currentUrl: String,
    onUrlChanged: (String) -> Unit,
    onGoClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // 这个组件也是无状态的，完全由外部驱动
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
                value = currentUrl,
                onValueChange = onUrlChanged,
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
                        // 点击后收起键盘
                        focusManager.clearFocus()
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
                    // 点击后收起键盘
                    focusManager.clearFocus()
                },
                modifier = Modifier.height(35.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("进入", fontSize = 13.sp)
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