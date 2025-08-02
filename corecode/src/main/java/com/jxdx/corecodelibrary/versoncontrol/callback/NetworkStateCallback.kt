package com.jxdx.corecodelibrary.versoncontrol.callback

import com.jxdx.corecodelibrary.versoncontrol.bean.NetworkType

interface NetworkStateCallback {
    fun onNetworkChanged(
        networkType: NetworkType,
    )
}