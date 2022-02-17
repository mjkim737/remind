package com.delightroom.reminder.global.util

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

//todo mj
class SingleLiveEvent<T>: MutableLiveData<T>() {
    private val pending = AtomicBoolean(false) //멀티쓰레딩 환경에서 동시성을 보장하는 AtomicBoolean.

    @MainThread
    override fun setValue(value: T?) {
        pending.set(true)
        super.setValue(value)
    }

    // View(Activity or Fragment 등 LifeCycleOwner)가 활성화 상태가 되거나. setValue로 값이 바뀌었을 때 호출되는 observe 함수.
    // setValue를 통해서만 pending값이 true로 바뀌기 때문에,
    // Configuration Changed가 일어나도 pending값은 false이기 때문에 observe가 데이터를 전달하지 않는다!
    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, Observer {
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(it)
            }
        })
    }

    //데이터의 속성을 지정해주지 않아도 call만으로 setValue를 호출 가능
    @MainThread
    fun call() {
        value = null
    }
}