package com.seven.colink.util.status

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

sealed class LiveDataObserver<out T> {
    data object Stop: LiveDataObserver<Nothing>()
    data class Start<T>(val data: T): LiveDataObserver<T>()
}

fun <T> observeLiveData(
    liveDataObserver: LiveDataObserver<T>,
    liveData: MutableLiveData<T>,
    observer: Observer<T>,
    lifecycleOwner: LifecycleOwner
) {
    when (liveDataObserver) {
        is LiveDataObserver.Stop -> liveData.removeObserver(observer)
        is LiveDataObserver.Start -> {
            liveData.observe(lifecycleOwner, observer)
            liveData.value = liveDataObserver.data
        }
    }
}