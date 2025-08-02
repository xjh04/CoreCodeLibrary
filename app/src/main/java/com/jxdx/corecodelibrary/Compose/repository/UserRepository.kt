package com.jxdx.corecodelibrary.Compose.repository

import com.jxdx.corecodelibrary.Compose.bean.UserInfo
import com.jxdx.corecodelibrary.Compose.network.ComposeApiService
import com.jxdx.corecodelibrary.Compose.network.NetworkUser
import com.jxdx.corecodelibrary.http.HttpManager

class UserRepository{

    private val apiService = HttpManager.instance.createRetrofit(ComposeApiService::class.java)
    /**
     * 获取用户信息。
     * 隐藏了数据来源的细节。
     * @param userId 要查询的用户 ID
     * @param oaid 从本地 SDK 获取的 oaid
     */
    suspend fun getUserInfo(userId: String, oaid: String): Result<UserInfo> {
        return try {
            // 1. 发起网络请求
            val networkUser = apiService.getUserInfo(userId)

            // 2. 将网络模型转换为领域模型
            val userInfo = networkUser.toDomainModel(oaid)

            // 3. (可选) 将获取到的数据缓存到 Room 数据库
            // userDao.insert(userInfo)

            // 4. 返回成功结果
            Result.success(userInfo)
        } catch (e: Exception) {
            // 5. 如果发生任何异常（网络错误、解析错误等），返回失败结果
            // (可选) 尝试从本地缓存读取旧数据
            // val cachedUser = userDao.getById(userId)
            // if (cachedUser != null) Result.success(cachedUser) else Result.failure(e)
            Result.failure(e)
        }
    }
}

// 将网络模型转换为领域模型的扩展函数
fun NetworkUser.toDomainModel(oaid: String): UserInfo {
    return UserInfo(
        nickname = this.name,
        uid = this.userId,
        deviceModel = this.device,
        androidVersion = this.osVersion,
        oaid = oaid // oaid 通常从本地 SDK 获取
    )
}