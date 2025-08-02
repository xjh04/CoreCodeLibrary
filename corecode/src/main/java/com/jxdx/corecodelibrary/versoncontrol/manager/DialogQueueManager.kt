package com.jxdx.corecodelibrary.versoncontrol.manager

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.jxdx.corecodelibrary.versoncontrol.bean.VersionUpdateTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.lang.ref.WeakReference

class DialogQueueManager private constructor(): ActivityLifecycleCallbacks{

    companion object{
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            DialogQueueManager()
        }
    }

    private var pageContext: WeakReference<Activity>? = null
    private val dialogQueue: ArrayDeque<() -> AlertDialog?> = ArrayDeque()
    private var isDialogShowing = false
    private var currentDialog: AlertDialog? = null

    fun init(context: Application){
        context.registerActivityLifecycleCallbacks(this)
    }

    fun showIfDeleteDialog(onDelete: () -> Unit) {
        addToQueue { context ->
            AlertDialog.Builder(context)
                .setTitle("确认删除")
                .setMessage("删除之后将永远无法回复！")
                .setCancelable(true)
                .setPositiveButton("确定") { _, _ ->
                    onDelete()
                }
                .setNegativeButton("取消") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
        }
    }

    fun showRenewDialog(task: VersionUpdateTask) {
        addToQueue { context ->
            AlertDialog.Builder(context)
                .setTitle("版本更新")
                .setMessage("您有新版本可以更新")
                .setCancelable(task.mandatoryUpdate != 1)
                .setPositiveButton("确定") { _, _ ->
                    Log.d("showRenewDialog", "新版本下载中")
                    pageContext?.get()?.let { VersionManager.getInstance(it)
                        .goToDownload(task,it) }
                }
                .create()
        }
    }

    fun showInstallDialog(apkFile: File,task: VersionUpdateTask) {
        addToQueue { context ->
            AlertDialog.Builder(context)
                .setTitle("版本更新")
                .setMessage("新版本${task.latestVersion}已为您下载（wifi环境）完成，快来安装吧")
                .setCancelable(task.mandatoryUpdate != 1)
                .setPositiveButton("确定") { _, _ ->
                    pageContext?.get()?.let {
                        CoroutineScope(Dispatchers.IO).launch {
                            VersionManager.getInstance(it)
                                .tryInstallApk(apkFile,context, task)
                        }
                    }
                }
                .create()
        }
    }

    fun showPackageInstallPermissionsDialog(packageName: String) {
        addToQueue { context ->
            AlertDialog.Builder(context)
                .setTitle("安装新版本权限请求")
                .setMessage("请前往设置打开安装权限，以保障APP正常使用")
                .setCancelable(true)
                .setPositiveButton("确定") { _, _ ->
                    val intent = Intent(
                        Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                        ("package:$packageName").toUri()
                    )
                    ContextCompat.startActivity(context, intent, null)
                }
                .create()
        }
    }

    fun showKickDialog() {
        addToQueue { context ->
            AlertDialog.Builder(context)
                .setTitle("账号异地登录")
                .setMessage("您的账号已在其他设备登录，请重新登录")
                .setCancelable(false)
                .setPositiveButton("确定") { _, _ ->
//                    TokenManager.loginOut()
//                    TokenManager.gotoLogin()
                }
                .create()
        }
    }

    fun showCodeChangedDialog() {
        addToQueue { context ->
            AlertDialog.Builder(context)
                .setTitle("账号状态异常")
                .setMessage("您的账号密码已经修改，请重新登录")
                .setCancelable(false)
                .setPositiveButton("确定") { _, _ ->
//                    TokenManager.loginOut()
//                    TokenManager.gotoLogin()
                }
                .create()
        }
    }

    fun showDisableDialog() {
        addToQueue { context ->
            AlertDialog.Builder(context)
                .setTitle("身份验证失效")
                .setMessage("您身份验证失效，请重新登录")
                .setCancelable(false)
                .setPositiveButton("确定") { _, _ ->
//                    TokenManager.loginOut()
//                    TokenManager.gotoLogin()
                }
                .create()
        }
    }

    private fun addToQueue(dialogCreator: (Activity) -> AlertDialog?) {
        val context = pageContext?.get() ?: run {
            Log.w("DialogManager", "Activity context is null")
            return
        }

        dialogQueue.add {
            val dialog = dialogCreator(context)?.apply {
                setOnDismissListener {
                    isDialogShowing = false
                    currentDialog = null
                    showNextDialog()
                }
            }
            currentDialog = dialog
            dialog
        }

        if (!isDialogShowing) {
            showNextDialog()
        }
    }

    private fun showNextDialog() {
        if (isDialogShowing) return

        val context = pageContext?.get() ?: run {
            clearQueue()
            return
        }

        if (context.isFinishing || context.isDestroyed) {
            clearQueue()
            return
        }

        while (dialogQueue.isNotEmpty()) {
            val dialogBuilder = dialogQueue.removeFirst()
            val dialog = dialogBuilder.invoke()

            if (dialog != null && !context.isFinishing && !context.isDestroyed) {
                isDialogShowing = true
                dialog.show()
                return
            }
        }
    }

    private fun clearQueue() {
        dialogQueue.clear()
        currentDialog?.dismiss()
        currentDialog = null
        isDialogShowing = false
    }



    // ActivityLifecycleCallbacks
    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        pageContext = WeakReference(p0)
    }

    override fun onActivityStarted(p0: Activity) {

    }

    override fun onActivityResumed(p0: Activity) {
        pageContext = WeakReference(p0)
    }

    override fun onActivityPaused(p0: Activity) {

    }

    override fun onActivityStopped(p0: Activity) {

    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

    override fun onActivityDestroyed(p0: Activity) {

    }
}