package com.jxdx.corecodelibrary.versoncontrol.bean

import com.jxdx.corecodelibrary.versoncontrol.manager.DownloadManager


open class DownloadTask(val taskId: Long, val downloadUrl: String, var isSilentDownload: Boolean) {
    //初始为暂停状态
    var state: Int = DownloadManager.STATE_PAUSED
}