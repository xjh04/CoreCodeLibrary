package com.jxdx.corecodelibrary

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.jxdx.corecodelibrary.http.BaseResponseState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class MainViewModel(application: Application) : BaseViewModel(application) {
    private val repository: MyRepository by lazy {
        MyRepository()
    }
    val ifLogin = repository.isLogin().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000),
        BaseResponseState.Loading()
    )

}