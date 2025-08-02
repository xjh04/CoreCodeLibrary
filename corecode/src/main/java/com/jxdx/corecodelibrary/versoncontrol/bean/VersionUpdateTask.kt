package com.jxdx.corecodelibrary.versoncontrol.bean

class VersionUpdateTask(
    taskId: Long,
    downloadUrl: String,
    isSilentDownload: Boolean,
    val latestVersion: String,
    val mandatoryUpdate: Int
) :
    DownloadTask(taskId, downloadUrl, isSilentDownload)