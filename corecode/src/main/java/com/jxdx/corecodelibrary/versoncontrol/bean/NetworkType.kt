package com.jxdx.corecodelibrary.versoncontrol.bean

enum class NetworkType {
    WIFI,          // WiFi 网络
    CELLULAR,      // 蜂窝移动网络（4G/5G）
    OTHER,         // 其他类型（以太网、VPN 等）
    DISCONNECTED,  // 无网络连接
    UNKNOWN        // 初始未知状态
}