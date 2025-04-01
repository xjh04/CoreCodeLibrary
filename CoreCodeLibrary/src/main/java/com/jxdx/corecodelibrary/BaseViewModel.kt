package com.jxdx.corecodelibrary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel

open class BaseViewModel(application: Application) : AndroidViewModel(application){
    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}