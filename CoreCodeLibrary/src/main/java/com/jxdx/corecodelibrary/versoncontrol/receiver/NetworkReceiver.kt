package com.jxdx.corecodelibrary.versoncontrol.receiver

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.jxdx.corecodelibrary.versoncontrol.bean.NetworkType
import com.jxdx.corecodelibrary.versoncontrol.callback.NetworkStateCallback


/**
 * 监听网络状态并回调 NetworkStateCallback
 */

class NetworkReceiver private constructor(context: Context): ConnectivityManager.NetworkCallback() {
    companion object{
        private var instance: NetworkReceiver? = null

        fun getInstance(context: Context): NetworkReceiver {
            return instance ?: synchronized(this) {
                instance ?: NetworkReceiver(context).also {
                    instance = it
                }
            }
        }
    }

    // 网络类型检测回调接口
    private val callbacks = mutableListOf<NetworkStateCallback>()

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // 当前网络状态跟踪（默认 UNKNOWN）
    private var currentNetworkType: NetworkType = NetworkType.UNKNOWN
    private var isMonitoring = false

    // 注册监听器
    fun addListener(callback: NetworkStateCallback) {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback)
        }
    }

    // 开始监听网络变化
    fun startMonitoring(){
        if (isMonitoring) {
            Log.d("NetworkState--", "网络状态监听器已注册")
            return
        }
        isMonitoring = true

        connectivityManager.registerDefaultNetworkCallback(this)
        Log.d("NetworkState--", "网络状态监听器注册成功")
    }


    // 停止监听
    fun stopMonitoring(){
        connectivityManager.unregisterNetworkCallback(this)
        Log.d("NetworkState--", "网络状态监听器关闭成功")
    }



    override fun onAvailable(network: Network) = updateNetworkState(network)

    /**
     * 当网络永久丢失时触发（非瞬时断开）
     * 当onLost触发之后 connectivityManager.activeNetwork 和 network 有以下特点
     * 网络已断开，系统可能已清除其能力信息，此时通过 network 获取的 caps 为 null
     * 但是也有可能未及时清除，可能返回断开前的最后能力信息（但此时网络已不可用）
     * 但是这里delay(100)测试效果 就是清除后的数据
     */
    override fun onLost(network: Network) {
        CoroutineScope(Dispatchers.Main).launch {
            delay(100)
            updateNetworkState(network)
        }
    }

    override fun onCapabilitiesChanged(
        network: Network,
        networkCapabilities: NetworkCapabilities
    ) = updateNetworkState(network)

    private fun updateNetworkState(network: Network?) {
        // 获取当前网络的 Capabilities 对象（包含网络能力信息）
        val caps = network?.let { connectivityManager.getNetworkCapabilities(it) }

        Log.d("NetworkState--", "当前的网络状态：${currentNetworkType.name}")

        // 判断新网络状态
        val newNetworkType = when {
            // 场景 1：无可用网络
            network == null -> {
                Log.d("NetworkState--", "无可用网络")
                NetworkType.DISCONNECTED
            }

            // 场景 2：网络能力异常
            caps == null -> {
                Log.d("NetworkState--", "网络能力为空")
                NetworkType.DISCONNECTED
            }

            // 场景 3：网络未通过系统验证
            !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) -> {
                Log.d("NetworkState--", "网络未通过验证")
                NetworkType.DISCONNECTED
            }

            // 场景 4：有效网络类型判断
            else -> when {
                // WiFi 网络
                caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    Log.d("NetworkState--", "已连接已验证的WiFi")
                    NetworkType.WIFI
                }

                // 蜂窝网络（移动数据）
                caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    Log.d("NetworkState--", "已切换至蜂窝数据")
                    NetworkType.CELLULAR
                }

                // 其他类型（如蓝牙、VPN、以太网等）
                else -> {
                    Log.d("NetworkState--", "其他网络类型")
                    NetworkType.OTHER
                }
            }
        }

        // 状态变化处理 回调处理
        if (currentNetworkType != newNetworkType) {
            Log.d("NetworkState--", "状态变化: ${currentNetworkType.name} → ${newNetworkType.name}")
            currentNetworkType = newNetworkType

            callbacks.forEach { callbacks ->
                callbacks.onNetworkChanged(
                    newNetworkType
                )
            }
        }
    }
}